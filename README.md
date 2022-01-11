![BlockMod](./src/main/resources/blockmod.png)

# BlockMod: Player Blocking

## Overview

BlockMod is a relatively simple Minecraft mod that allows you to visually block players while still allowing you to play
with them. By default, this works by obfuscating any text that contains a blocked player's name (nameplate, text chat,
etc.) and replacing that player's skin with a generic "blocked" version of the default steve/alex skin. The player
entity can optionally be replaced with a simple billboard texture instead, if desired.

The target audience for this mod is content creators who don't want a particular player from appearing in their content,
but also still want to be able to play with them for any reason. That said, anyone is allowed to use this mod, and
suggestions to improve it are encouraged.

## How To Use

Currently, BlockMod lacks an interface to configure it easily. Until that gets added, you'll have to edit the config
files manually.

BlockMod allows you to block players on both the client and the server, depending on where the mod is installed. Players
blocked on the client will always appear blocked, while players blocked on the server will only appear blocked when
you're on that server. On this plus side, blocking players on the server means that users don't have to configure their
blocklist themselves. (Note that players blocked on the server will not appear blocked if you don't have BlockMod
installed on your client. I'd like to add a workaround for this in the future.)

To modify the client settings, go into the `configs` folder and edit `blockmod-client.toml`. To modify the client
blocklist, edit `blockmod-blocklist-client.toml` instead (this allows you to share blocklists without also sharing your
personal settings).

Currently, the server doesn't have any settings other than the blocklist. To modify the server blocklist, go into the
world's folder, then into `serverconfig`, and edit `blockmod-blocklist-server.toml`. Also, the server-side blocklist can
be modified using commands. Do `/blockmod` and see what sub-commands are available. Only people who are opped will be
able to use the `/blockmod block` and `/blockmod unblock` commands. You can also do `/bm` instead of `/blockmod` to be
faster.

All of these `.toml` files will generate automatically after the game starts. If you don't see these files after
installing the mod, be sure to run the game first.

## FAQ

- Do I need this mod on both the client and the server?
    - No, BlockMod will load just fine if it's only on one side, though this comes with some caveats.
        - If the client has the mod, but the server doesn't, then the client will still be able to block players through
          its client-side config. The `/blockmod` command will not be available in this case.
        - If the server has the mod, but a client doesn't, then the client will not block any players (but it can still
          connect even without the mod).
- Doesn't vanilla already let you do this (via the "Social Interactions" menu)?
    - Yes and no. The vanilla game lets you hide *messages* from certain players, but it doesn't hide them from the game
      in general.
    - The game also lets you block players via your Microsoft account, but all this does is block Realms invites from
      them.

## Known Issues

- When you type a blocked person's name into chat, it won't appear blocked until the message is sent. This means that
  some command suggestions will leak a blocked person's name if you aren't careful.
- Adding people to the blocklists isn't always retroactive in chat (old messages may still appear unblocked)
    - As a workaround, use `F3 + D` to clear the chat history. Also consider restarting the game when the list changes
      if you want to absolutely make sure.

## Future Plans

- A whitelist feature that blocks everyone not on the whitelist, and/or allows clients to override the server blocklist
  for themselves
- An interface for configuring the mod and modifying the lists
- Billboard rendering that still shows what the player is holding/wearing
- Optionally, automatically block players on the list in the vanilla Social Interactions menu
- Make server-side blocking *sorta* work when a client doesn't have the mod (obfuscate the name, optionally hide
  messages, etc.)