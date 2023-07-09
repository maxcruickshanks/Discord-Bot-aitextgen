# Discord Bot (Using aitextgen)
A Discord bot that can be trained on your server's messages to simulate conversations.

Note that this LLM uses GPT-2 configurations, so it is limited in generating coherent (non-hallucinations) messages.

# Overview
This Discord bot was created using Java and [JDA](https://github.com/discord-jda/JDA) with the text generation and training done through modified Python code from [aitextgen](https://github.com/minimaxir/aitextgen).

Currently, it generates text whenever pinged or every `MESSAGE_FREQUENCY` messages:
![Example of bot text generation](https://i.gyazo.com/0192c87199ec93f93ba2477af0ac205f.png)

# Installation
For installation, it is recommended to use `WSL` or `Ubuntu`.

It is also strongly recommended to have a dedicated GPU for training since training on a CPU is magnitudes slower.

If you are going to use Windows, you will need to modify the code for `LLM.generate_message` and `LLM.python_training`.

## WSL/Ubuntu
First, clone the repository (or download it): `git clone https://github.com/maxcruickshanks/discord-bot-aitextgen.git`

Second, install the required pip packages: `pip install -r requirements.txt`

Third, update the `GUILD_ID` and `CHANNEL_TO_TRAIN` in the `Constants.java` file to the guild id and text channel id you want to train using aitextgen.

Fourth, compile the bot to a JAR file (within this repository, so it contains `/aitextgen/`).

Fifth, create a text file called `TOKEN.txt` in the same folder as the JAR file (and this project) with the Discord token of your bot from [here](https://discord.com/developers/applications/).
![Discord token location](https://i.gyazo.com/356884038b0463e14cd99b8a0ed92189.png)

(Optional) Tune `max_epochs` in `aitextgen.train` if you want it to be more accurate.

Sixth, run the bot, train it, and enjoy your bot!

(Optional) Finally, comment the `LLM.train_user(true)` line in `Main.java` and re-compile to prevent re-training when restarting the bot.
