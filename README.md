# Resmat — entites et repositories

## Quel dossier ouvrir dans Eclipse ?

Importer **le pom.xml a la racine de ce depot** avec File > Import > Maven > Existing Maven Projects.
Le dossier `res-mat-ini-main` est une ancienne copie conservee telle quelle : ne pas l'importer en plus.
Les modifications de cette etape sont dans le dossier `src` a la racine.

## Ce qui est realise

- Cinq entites JPA, avec constructeur sans argument et getters/setters Lombok.
- Cinq interfaces JpaRepository : save, findAll, findById, deleteById sont deja fournis par Spring Data.
- TypeMateriel conserve son identifiant UUID. Les quatre autres entites utilisent Long.
- Role, StatutMateriel et StatutReservation sont des enums stockees sous forme de texte.
- Contraintes : champs obligatoires, email unique, dates ordonnees, un seul emprunt par reservation.
- Pas de suppression en cascade vers l'historique : les cles etrangeres protegent les references.

| Association | Signification |
| --- | --- |
| Materiel → TypeMateriel | Plusieurs materiels pour un type/categorie |
| Reservation → Utilisateur (emprunteur) | Un emprunteur pour chaque demande |
| Reservation → Utilisateur (gestionnaire) | Gestionnaire facultatif avant traitement |
| Reservation → Materiel | Un materiel pour chaque demande |
| Emprunt → Reservation | Au maximum un emprunt par reservation |

Les associations sont unidirectionnelles : les cles etrangeres suffisent pour cette etape.
TypeMateriel tient le role de categorie (camera, ordinateur, micro...). Les anciennes classes Categorie et Statut de la copie ne sont pas importees.
`quantite` est le stock total, pas une disponibilite calculee. `dureeMaxEmprunt` est en jours, 0 veut dire sans limite ; null sur Materiel signifie utiliser celle du type.

## Corrections apportees

- Suppression de l'espace avant la declaration XML du pom.xml.
- Entites ajoutees au bon dossier source Maven et association Materiel/TypeMateriel ajoutee.
- Statut explicite pour Reservation, a la place d'un avis libre.
- Configuration de connexion par variables d'environnement.
- Desactivation par defaut des traces de parametres SQL.

La version Spring Boot 4.1.2 du depot a ete conservee.

## Lancer sous Windows avec PowerShell

Installer Java 17 et Docker Desktop, puis demarrer Docker Desktop.
Dans le dossier contenant pom.xml et compose.yml :

```powershell
$env:DB_USER = "resmat"
$env:DB_PASSWORD = "choisir_un_mot_de_passe_local"
$env:DB_ROOT_PASSWORD = "choisir_un_autre_mot_de_passe_local"
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Pour lancer depuis Eclipse, definir DB_PASSWORD (et DB_USER si change) dans Run Configurations > Environment.
On peut aussi copier .env.example vers .env pour Docker Compose ; Spring Boot ne charge pas automatiquement ce fichier, ses variables doivent aussi etre definies dans Eclipse ou PowerShell.

**Si le volume MariaDB existe deja**, conserver ses vrais identifiants. Modifier les variables Compose ne change pas les comptes deja crees. Ne pas supprimer le volume pour contourner cela si des donnees sont a conserver.

Dans DBeaver : hote localhost, port 3306, base resmat, utilisateur DB_USER, mot de passe DB_PASSWORD.
Au demarrage, Hibernate cree/met a jour les cinq tables. Si la base vient de l'ancienne copie, sauvegarder les donnees avant de traiter les differences de schema : ddl-auto=update ne migre pas automatiquement Categorie/Statut vers le nouveau modele.

Il n'y a pas encore de page d'accueil ni de CRUD : cette livraison couvre les etapes 1 et 2.

## Tests

```powershell
.\mvnw.cmd test
```

Les tests utilisent une base H2 en memoire, sans toucher a MariaDB : enregistrement et relecture des cinq entites liees, unicite reservation/emprunt, protection de l'historique, dates inversees ou egales, calcul du retard, chargement du contexte Spring.

Pour tester les memes associations avec **une base MariaDB de test vide et separee**, creer cette base puis lancer :

```powershell
.\mvnw.cmd test "-Dspring.datasource.url=jdbc:mariadb://localhost:3306/resmat_test" "-Dspring.datasource.driver-class-name=org.mariadb.jdbc.Driver" "-Dspring.datasource.username=resmat" "-Dspring.datasource.password=$env:DB_PASSWORD" "-Dspring.jpa.hibernate.ddl-auto=create-drop"
```

L'utilisateur SQL doit avoir des droits sur resmat_test. Ne jamais viser la base principale : create-drop cree puis supprime les tables de test.

Verification dans l'environnement de preparation : lecture du depot et verification du XML effectuees. Execution Maven bloquee par l'impossibilite de resoudre repo.maven.apache.org ; compilation et tests restent a executer sur ton poste. Le schema MariaDB n'a pas ete verifie ici.

## Etapes suivantes

Donnees de demonstration, services/DTO/controllers/templates du CRUD, puis Spring Security.
Les roles et statuts sont prets, mais les autorisations, l'acceptation/refus, les chevauchements, la disponibilite et les transitions de statut devront etre controles dans les services. Un mapping JPA seul ne fait pas respecter ces regles.
