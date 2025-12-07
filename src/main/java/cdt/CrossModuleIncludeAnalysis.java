package cdt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 跨模块 include 调用分析工具
 */
public class CrossModuleIncludeAnalysis {

    /**
     * 运行分析模块
     *
     * @param jsonFilePath    输入 JSON 文件路径
     * @param targetDirectory 目标目录（如 libuv）
     * @param outputJsonPath  输出 JSON 文件路径
     */
    public static void analyze(String jsonFilePath, String targetDirectory, String outputJsonPath) {
        try {
            // 读取 JSON 文件
            String jsonContent = readFile(jsonFilePath);

            // 转换为 JSON 对象
            JSONObject jsonObject = new JSONObject(jsonContent);

            // 提取 variables 和 relations 数组
            JSONArray variables = jsonObject.getJSONArray("variables");
            JSONArray relations = jsonObject.getJSONArray("relations");

            // 获取指定目录下的所有 File 分类的 ID
            List<Integer> fileIds = getFileIdsInDirectory(variables, targetDirectory);

            // 获取所有 entityFile 等于找到的 fileId 的 entity 的 ID
            Map<Integer, List<Integer>> fileToEntitiesMap = getEntitiesGroupedByFile(variables, fileIds);

            // 获取按 fileID 划分并过滤后的关系信息
            Map<Integer, Map<Integer, List<Map<String, Object>>>> structuredResults =
                    getFileBasedStructuredResults(relations, fileToEntitiesMap);

            // 过滤掉 fileID 和 fromFileID 相同的关系
            Map<Integer, Map<Integer, List<Map<String, Object>>>> filteredResults =
                    filterSameFileRelationships(structuredResults);

            // 输出结果到 JSON 文件
            saveFileBasedResultsToJson(filteredResults, outputJsonPath);

            System.out.println("Cross-module analysis results saved to: " + outputJsonPath);

        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing the JSON: " + e.getMessage());
        }
    }

    /**
     * 读取文件内容为字符串
     */
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append('\n');
            }
        }
        return content.toString();
    }

    /**
     * 获取指定目录下所有 File 分类的 id
     */
    private static List<Integer> getFileIdsInDirectory(JSONArray variables, String targetDirectory)
            throws JSONException {

        List<Integer> fileIds = new ArrayList<>();

        for (int i = 0; i < variables.length(); i++) {
            JSONObject variable = variables.getJSONObject(i);

            if ("File".equals(variable.optString("category"))) {
                String qualifiedName = variable.optString("qualifiedName");
                if (qualifiedName.startsWith(targetDirectory + "/")) {
                    fileIds.add(variable.getInt("id"));
                }
            }
        }
        return fileIds;
    }

    /**
     * 获取所有 entityFile 在目标 fileIds 内的实体，并按 fileId 分组
     */
    private static Map<Integer, List<Integer>> getEntitiesGroupedByFile(JSONArray variables, List<Integer> fileIds)
            throws JSONException {

        Map<Integer, List<Integer>> fileToEntitiesMap = new HashMap<>();
        for (Integer fileId : fileIds) {
            fileToEntitiesMap.put(fileId, new ArrayList<>());
        }

        for (int i = 0; i < variables.length(); i++) {
            JSONObject variable = variables.getJSONObject(i);
            int entityFileId = variable.optInt("entityFile", -1);
            if (fileToEntitiesMap.containsKey(entityFileId)) {
                fileToEntitiesMap.get(entityFileId).add(variable.getInt("id"));
            }
        }
        return fileToEntitiesMap;
    }

    /**
     * 获取按 fileID 划分的关系信息，并过滤掉 libuv 内部文件之间的调用
     *
     * @param relations         全量关系
     * @param fileToEntitiesMap libuv 目录下文件 → 其实体列表
     */
    private static Map<Integer, Map<Integer, List<Map<String, Object>>>> getFileBasedStructuredResults(
            JSONArray relations,
            Map<Integer, List<Integer>> fileToEntitiesMap) throws JSONException {

        // libuv 目录下全部文件 ID，用来判定调用方是否也在 libuv
        Set<Integer> targetFileIds = new HashSet<>(fileToEntitiesMap.keySet());

        Map<Integer, Map<Integer, List<Map<String, Object>>>> structuredResults = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : fileToEntitiesMap.entrySet()) {
            int fileId = entry.getKey();                 // 被调用文件（位于 libuv）
            List<Integer> entityIds = entry.getValue();  // 该文件内实体

            Map<Integer, List<Map<String, Object>>> fileToFromDetails = new HashMap<>();

            for (int i = 0; i < relations.length(); i++) {
                JSONObject relation = relations.getJSONObject(i);

                int toEntityId = relation.optInt("to", -1);
                int fromEntityId = relation.optInt("from", -1);
                JSONObject loc = relation.optJSONObject("loc");
                if (loc == null || !entityIds.contains(toEntityId)) {
                    continue;   // to 端不在目标文件
                }

                int fromFileId = loc.optInt("file", -1);

                // 若调用方 also 位于 libuv 目录，则过滤掉
                if (targetFileIds.contains(fromFileId)) {
                    continue;   // keep only cross-module callers
                }

                Map<String, Object> relationDetails = new HashMap<>();
                relationDetails.put("fromID", fromEntityId);
                relationDetails.put("toID", toEntityId);
                relationDetails.put("line", loc.optInt("line", -1));

                fileToFromDetails
                        .computeIfAbsent(fromFileId, k -> new ArrayList<>())
                        .add(relationDetails);
            }

            if (!fileToFromDetails.isEmpty()) {
                structuredResults.put(fileId, fileToFromDetails);
            }
        }
        return structuredResults;
    }

    /**
     * 过滤掉 fileID 与 fromFileID 相同的关系（同文件自调用）
     */
    private static Map<Integer, Map<Integer, List<Map<String, Object>>>> filterSameFileRelationships(
            Map<Integer, Map<Integer, List<Map<String, Object>>>> structuredResults) {

        Map<Integer, Map<Integer, List<Map<String, Object>>>> filteredResults = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, List<Map<String, Object>>>> fileEntry : structuredResults.entrySet()) {
            int fileId = fileEntry.getKey();
            Map<Integer, List<Map<String, Object>>> fromMap = fileEntry.getValue();

            Map<Integer, List<Map<String, Object>>> filteredFromMap = new HashMap<>();
            for (Map.Entry<Integer, List<Map<String, Object>>> fromEntry : fromMap.entrySet()) {
                int fromFileId = fromEntry.getKey();
                if (fromFileId == fileId) {
                    continue;
                }
                filteredFromMap.put(fromFileId, fromEntry.getValue());
            }

            if (!filteredFromMap.isEmpty()) {
                filteredResults.put(fileId, filteredFromMap);
            }
        }
        return filteredResults;
    }

    /**
     * 将结果保存到 JSON 文件
     */
    private static void saveFileBasedResultsToJson(
            Map<Integer, Map<Integer, List<Map<String, Object>>>> structuredResults,
            String outputJsonPath) throws IOException, JSONException {

        JSONObject outputJson = new JSONObject();

        for (Map.Entry<Integer, Map<Integer, List<Map<String, Object>>>> fileEntry : structuredResults.entrySet()) {
            int fileId = fileEntry.getKey();
            Map<Integer, List<Map<String, Object>>> fromFileDetails = fileEntry.getValue();

            JSONArray fromFileArray = new JSONArray();
            for (Map.Entry<Integer, List<Map<String, Object>>> fromEntry : fromFileDetails.entrySet()) {
                JSONObject fromObj = new JSONObject();
                fromObj.put(String.valueOf(fromEntry.getKey()), fromEntry.getValue());
                fromFileArray.put(fromObj);
            }
            outputJson.put(String.valueOf(fileId), fromFileArray);
        }

        try (FileWriter fw = new FileWriter(outputJsonPath)) {
            fw.write(outputJson.toString(4));
        }
    }
}
