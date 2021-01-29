package com.github.dwursteisen.minigdx.file

class AngelCodeLoader : FileLoader<AngelCode> {

    @ExperimentalStdlibApi
    override fun load(filename: String, handler: FileHandler): Content<AngelCode> = handler.platformFileHandler.read(filename).map {
        val groups = it.split("\r\n|\n|\r".toRegex())
            .filter { it.isNotBlank() }
            .groupBy { it.split(" ").first() }

        val info = groups.getValue("info")
            .first()
            .asMap()

        val common = groups.getValue("common")
            .first()
            .asMap()

        val page = groups.getValue("page")
            .first()
            .asMap()

        val chars = groups.getValue("chars")
            .first()
            .asMap()

        val char = groups["char"]?.map { line ->
            val dic = line.asMap()

            val id = dic.getValue("id").toInt().toChar()
            id to AngelCharacter(
                id = id,
                x = dic.getValue("x").toInt(),
                y = dic.getValue("y").toInt(),
                width = dic.getValue("width").toInt(),
                height = dic.getValue("height").toInt(),
                xoffset = dic.getValue("xoffset").toInt(),
                yoffset = dic.getValue("yoffset").toInt(),
                xadvance = dic.getValue("xadvance").toInt()
            )
        }?.toMap() ?: emptyMap()

        val fontInfo = FontInfo(
            face = info.getValue("face").replace("\"", ""),
            size = info.getValue("size").toInt(),
            lineHeight = common.getValue("lineHeight").toInt(),
            base = common.getValue("base").toInt(),
            charsCount = chars.getValue("count").toInt(),
            pages = common.getValue("pages").toInt(),
            fontFile = page.getValue("file").replace("\"", "")
        )

        AngelCode(
            info = fontInfo,
            characters = char
        )
    }

    private fun String.asMap(): Map<String, String> = split(" ")
        .drop(1) // drop header
        .filter { it.isNotBlank() }
        .map { field ->
            val (key, value) = field.split("=")
            key to value
        }.toMap()
}
