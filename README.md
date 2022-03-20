# PAM_minecraft

Application android permettant de récupérer des informations sur l'état de serveurs Minecraft:
- Version
- Nombre de joueurs
  - Actuel et total
- État
  - Hors ligne
  - En ligne

Fonctionnalités supplémentaires:
- Résolution DNS
  - Résolution SRV
- Persistance des serveurs enregistrés


Cette appli utilise notre implémentation du protocole Minecraft SLP ([ServerListPing](https://wiki.vg/Server_List_Ping)) pour interroger les serveurs. Celui-ci ouvre directement une socket sur le serveur Minecraft, il n'y a pas de requètes ou API HTTP.

Sur la page principale il y a la liste des serveurs déja enregistré et leurs dernier états. Il suffit de tapper sur le bouton "Update" pour forcer une mise à jour de tous les serveurs. L'icone "+" en bas à droite permet d'ajouter un nouveau serveur.
Lors de l'ajout il est possible de ne pas préciser l'@IP et le port si vous avez un nom de domaine ayant un enregistrement [SRV](https://en.wikipedia.org/wiki/SRV_record).

Le détail d'un serveur peut être visionné en cliquant sur celui-ci dans la liste. Depuis le détail il est possible de le supprimer.

Limitations:
De part notre implémentation du protocole SLP et la non-existance de documentation sur ce sujet, les serveurs Minecraft moddés ne sont pas pris en compte.
