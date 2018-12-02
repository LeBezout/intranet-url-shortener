# Raccourcisseur d'URL pour l'intranet

## A- Présentation du projet

### A.1- Objet

On constate régulièrement dans les différents sites intranet de l'entreprise (quelle qu'elle soit) l'utilisation de raccourcisseurs d'URL externes,
tels que ceux de Google ou bit.ly.

C'est un service effectivement très pratique et très populaire mais **qui n'est pas sans poser quelques problématiques**
et notamment dans une utilisation interne à l'entreprise.

### A.2- Qu'est-ce qu'un raccourcisseur d'URL ?

Un raccourcisseur ou réducteur d'URL est un générateur de liens courts, consistant à assigner une clé unique
de quelques caractères à une page web spécifique et permettant de rediriger vers l'URL d'origine.

Le protocole HTTP prévoit nativement la technique de redirection via notamment les statuts dans la plage `3XX` (généralement `301`, `302` ou `307`).

:information_source: **Un lien raccourci est donc plus facile à partager ou à mémoriser.**

A noter enfin que nombre d'applications, notamment dans le domaine des réseaux sociaux, possèdent des intégrations natives, 
on pourra citer YouTube, Facebook, Twitter. LinkedIn, Google.

### A.3- Les problématiques liées à l'utilisation des raccourcisseurs d'URL

* Une URL réduite offerte par un service Internet dans une utilisation interne à l'entreprise génère des flux réseaux inutiles : on sort de l'intranet pour y ré-entrer ensuite : `intranet -> proxy web -> service internet -> firewall -> intranet`
* Une URL réduite masque l'adresse originale. Les différents services offrent généralement la possibilité de prévisualiser le site de destination au lieu d'y être redirigé directement, ... mais qui le fait vraiment ?
* Il peut exister pour une même URL autant de liens courts différents qu'il existe de services de raccourcissement.
* Si le service externe de réduction d'URL ferme alors toutes les adresses réduites l'utilisant deviennent inaccessibles, il est de plus dès lors impossible d'obtenir l'adresse d'origine.
* Les services externes de réduction d'URL en profitent pour collecter des données et autres statistiques.
* Une URL peut être une donnée sensible, elle peut contenir des informations importantes en paramètres (login, jeton, voire mot de passe, ...).
* Dans le cas d'une utilisation en interne on peut indirectement exposer des informations concernant l'infrastructure interne d’une entreprise.

### A.4- Constat

Il n'existe à priori pas d'outil sur le marché que l'on puisse installer dans le réseau de l'entreprise pour rendre ce service.

C'est tout l'intérêt de ce projet de pouvoir être déployé facilement (via des images _Docker_) et utilisé dans l'intranet d'une entreprise.

## B- Description du projet

### B.1- Architecture

TODO `Browser->URL Shortener->Cible`

### B.2- Détail des composants

| Composant | Description | Technologies |
|---|---|---|
| Frontend Web | Interface permettant d'enregistrer, modifier, supprimer de nouveaux liens. Rechercher ou afficher le détaisl d'un lien. | Vue.js + Typescript dans un serveur NGINX |
| Backend REST | Exposer l'API nécessaire au frontend ainsi que la rédirection effective vers l'URL ciblée | Spring Boot + Tomcat Embedded |
| Base de données relationnelle | Stockage des éléments | H2, MySQL, MariaDB, PostGreSQL, ... |

## Annexes

### Annexe 1 : RFC du protocole HTTP

:bulb: [RFC HTTP Status - section-6.4.2](https://tools.ietf.org/html/rfc7231#section-6.4.2)

### Annexe 2 : détail des statuts HTTP de redirection

| Code | Message | Signification |
|---|---|---|
| `300` | **Multiple Choices** | L'URI demandée se rapporte à plusieurs ressources. |
| `301` | **Moved Permanently** | Document déplacé de façon permanente. |
| `302` | **Found** | Document déplacé de façon temporaire. |
| `303` | **See Other** | La réponse à cette requête est ailleurs. |
| `304` | **Not Modified** | Document non modifié depuis la dernière requête. |
| `305` | **Use Proxy** | (depuis HTTP/1.1) La requête doit être ré-adressée au proxy. |
| `306` | **Switch Proxy** | Code utilisé par une ancienne version de la RFC 26166, à présent réservé. Elle signifiait "Les requêtes suivantes doivent utiliser le proxy spécifié". |
| `307` | **Temporary Redirect** | La requête doit être redirigée temporairement vers l'URI spécifiée. |
| `308` | **Permanent Redirect** | La requête doit être redirigée définitivement vers l'URI spécifiée. |
| `310` | **Too many Redirects** | La requête doit être redirigée de trop nombreuses fois, ou est victime d’une boucle de redirection. |

Source : [Wikipedia](https://fr.wikipedia.org/wiki/Liste_des_codes_HTTP#3xx_-_Redirection)
