package no.mnemonic.act.platform.cli.tools;

import no.mnemonic.act.platform.cli.tools.commands.ReindexCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(
        description = "Command line tools for managing the application",
        name = "act-platform-cli-tools",
        mixinStandardHelpOptions = true,
        scope = CommandLine.ScopeType.INHERIT,
        subcommands = {ReindexCommand.class},
        showDefaultValues = true,
        usageHelpAutoWidth = true,
        versionProvider = Main.class
)
public class Main implements IVersionProvider {

  @Override
  public String[] getVersion() {
    String version = getClass().getPackage().getImplementationVersion();
    return new String[]{
            String.format("${COMMAND-FULL-NAME}: %s", version != null ? version : "UNKNOWN"),
            "JVM: ${java.version} (${java.vm.name} by ${java.vendor})"
    };
  }

  public static void main(String... args) {
    System.exit(new CommandLine(new Main()).execute(args));
  }
}
