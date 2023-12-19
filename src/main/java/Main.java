import cdt.TypeBinding;
import picocli.CommandLine;
import util.Configure;
import util.UnionFind;

import java.util.Arrays;
import java.util.Set;

public class Main {

	/**
	 * @methodsName: main
	 * @description: main function
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * 命令行参数处理
		 *
		 * @param args 命令行参数数组
		 */
		System.out.println(Arrays.toString(args));
		final Configure CliParam = new Configure();
		final CommandLine commandLine = new CommandLine(CliParam);
		try {
			final CommandLine.ParseResult parseResult = commandLine.parseArgs(args);
			if (parseResult.isUsageHelpRequested()) {
				commandLine.usage(System.out);
				System.exit(0);
			}
			if (parseResult.isVersionHelpRequested()) {
				commandLine.printVersionHelp(System.out);
				System.exit(0);
			}
		} catch (CommandLine.ParameterException e) {
			commandLine.usage(System.out);
			System.exit(1);
		}

		/**
		 * 处理程序的输入源路径、程序运行环境、项目名称
		 * 进行文件解析、类型绑定、依赖关系建立
		 * 最终生成相应的输出文件
		 * 并输出程序的执行消耗时间
		 *
		 * @param args 命令行参数
		 * @return 无返回值
		 */
		Configure configure = CommandLine.populateCommand(Configure.getConfigureInstance(),args);
		configure.dealWithInputSrcPath();
		String inputDir = configure.getInputSrcPath();
		String projectName = configure.getProjectName();
		Set<String> Program_environment = configure.getProgram_environment();

		long startTime = System.currentTimeMillis();

		Processor processor = new Processor(Program_environment);
		processor.parseAllFile(inputDir);
		System.out.println("Entity found finish!");
		processor.typeBinding();
		processor.dependencyBuild();
		processor.outputFile(projectName);
		System.out.println("Process finish!");

		long endTime = System.currentTimeMillis();
		System.out.println("Consumed time: " + (float) ((endTime - startTime) / 1000.00) + " s,  or "
				+ (float) ((endTime - startTime) / 60000.00) + " min.");
	}
}
