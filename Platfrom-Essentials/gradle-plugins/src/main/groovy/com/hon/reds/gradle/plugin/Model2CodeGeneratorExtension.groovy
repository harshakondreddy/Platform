package com.hon.reds.gradle.plugin

class Model2CodeGeneratorExtension {

	def modelDir = 'src/main/resources/models'
	def databaseFlag = 'ALL'
	def generatedTestDataFlag = 'true'
	def generationRule = 'protected'
	def clientType = 'dto'
}
