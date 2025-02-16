package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.AtomicBigInteger
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.stats.GlobalStats
import me.bristermitten.devdenbot.util.formatNumber
import net.dv8tion.jda.api.EmbedBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
@Used
class InfoCommand @Inject constructor(
    private val ddbConfig: DDBConfig,
) : DevDenCommand(
    name = "info",
    help = "View bot info and some cool stats",
    category = ManagingCategory,
    aliases = arrayOf("flex", "stats")
) {

    private val version by lazy {
        javaClass.classLoader.getResourceAsStream("version.txt")!!.reader().readText()
    }

    private fun formatDate(dt: LocalDate) = DateTimeFormatter.ofPattern("YYYY MM dd").format(dt)

    override suspend fun CommandEvent.execute() {
        fun formatForInfo(s: String) = "`$s`"
        fun formatForInfo(s: Number) = "`${formatNumber(s)}`"

        val totalXP = formatForInfo(StatsUsers.all.map { it.xp }.reduce(AtomicBigInteger::plus).get())
        val totalMembers = formatForInfo(event.guild.memberCount)
        val dateCreated = formatForInfo(formatDate(event.guild.timeCreated.toLocalDate()))
        val totalMessages = formatForInfo(GlobalStats.totalMessagesSent.get())
        val levelUps = formatForInfo(StatsUsers.all.map { it.level }.sumOf { it.get() })
        val formattedVersion = formatForInfo(version)

        reply(EmbedBuilder()
            .setTitle("Developer Den")
            .setDescription("Mildly interesting stats and info")
            .setColor(ddbConfig.colour)
            .addField("Version", formattedVersion, true)
            .addField("Total XP Given", totalXP, true)
            .addField("Total Members", totalMembers, true)
            .addField("Date Created", dateCreated, true)
            .addField("Total Messages Sent", totalMessages, true)
            .addField("Level Ups", levelUps, true)
            .build())
    }


}
