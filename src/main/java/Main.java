import cdt.CrossModuleIncludeAnalysis;
import cdt.TemplateWork;
import util.Configure;

public class Main {

	/**
	 * @methodsName: main
	 * @description: main function
	 */
	public static void main(String[] args) throws Exception {
		// 示例参数 - 修改为实际用户输入
		args = new String[] {
				"D:\\repo\\android\\icing\\icing\\jni\\test",
				"test"
//				"-c=libuv_1.48.0"
		};

		// 执行 ENRE-CPP 分析
		TemplateWork templateWork = new TemplateWork();
		templateWork.execute(args);

		// 获取配置实例
		Configure configure = Configure.getConfigureInstance();
		String crossModulePath = configure.getCrossModulePath();

		// 如果指定了 -c 参数，则执行 CrossModuleIncludeAnalysis
		if (crossModulePath != null && !crossModulePath.isEmpty()) {
			System.out.println("现在开始分析指定目录中接口的使用情况。");
			String projectName = configure.getProjectName();
			String jsonFilePath = "D:\\repo\\ENRE-cpp\\output" + projectName + "_out.json";
			String outputJsonPath = "D:\\repo\\ENRE-cpp\\output" + projectName + "-c_out.json";

			CrossModuleIncludeAnalysis.analyze(jsonFilePath, crossModulePath, outputJsonPath);
		}
	}
}
