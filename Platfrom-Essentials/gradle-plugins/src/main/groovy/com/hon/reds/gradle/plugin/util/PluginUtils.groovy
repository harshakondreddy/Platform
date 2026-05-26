package com.hon.reds.gradle.plugin.util

class PluginUtils {

	static readFile(String fileName, String taskName) {

		try {

			InputStream stream = this.getClassLoader().getResourceAsStream(fileName)

			if(stream != null) {

				StringBuilder sb = new StringBuilder()
				BufferedReader br = new BufferedReader(new InputStreamReader(stream))

				for(;;) {

					String line = br.readLine()

					if(line != null)
						sb.append(line).append(System.getProperty("line.separator"))
					else
						break
				}

				return sb.toString()
			}

			println "[" + taskName + "] " + "Resource " + fileName + " could not be found"
			return null
		} catch (Exception e) {
			println "[" + taskName + "] " + "Resource " + fileName + " could not be read"

			throw new RuntimeException(e)
		}
	}

	static writeFile(String content, String fileName, boolean replaceFile, String taskName) {

		try {

			File target = new File(fileName)
			File parent = target.getParentFile()

			if(parent != null && parent.exists()) {
				if(target.exists()) {
					if(replaceFile) {

						println "[" + taskName + "] " + "Target " + target.getAbsolutePath() + " exists. Overwriting file."
						target.delete()
					} else {
						println "[" + taskName + "] " + "Target " + target.getAbsolutePath() + " exists. Not overwriting file."
						return false
					}
				} else {
					println "[" + taskName + "] " + "Target " + target.getAbsolutePath() + " does not exists will generate it."
				}
			} else {
				println "[" + taskName + "] " + "Parent directory of " + target.getAbsolutePath() + " does not exists skipping."
				return false
			}

			new PrintStream(target).println(content)

			if(target.getAbsolutePath().toLowerCase().endsWith(".sh"))
				target.setExecutable(true, true)

			return true
		} catch (Exception e) {

			println "[" + taskName + "] " + "Artifact " + fileName + " could not be generated"
			throw new RuntimeException(e)
		}
	}
}