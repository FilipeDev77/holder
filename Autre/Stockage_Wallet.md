## Stockage sécurisé sur Android

**Objectif :** protéger les credentials (identités électroniques) stockés sur le smartphone afin qu’ils ne puissent pas être lus ou modifiés par des tiers.

**Points clés déjà définis :**

- Utilisation du **Secure Storage natif Android** (KeyStore + EncryptedSharedPreferences).

- Chaque credential est stocké de manière **chiffrée**.

- Accès possible **uniquement via le smartphone** (lié au hardware du device).

**Limitation actuelle :**

- Si le smartphone est perdu ou volé, l’identité électronique l’est aussi.

- Pas de backup ou de transfert simple.

## Wallet numérique

**Objectif :** stocker et gérer les **credentials numériques** (identité électronique) et permettre :

- Révélations sélectives d’attributs (ex: prouver que tu es majeur sans montrer ta date de naissance).

- Interactions avec des **verifiers** via ZKP (Zero Knowledge Proof).

**Format et stockage :**

- **Format JSON** pour chaque credential :

- Chaque attribut peut être associé à un **digest** pour permettre le **Selective Disclosure**.

- Le **wallet peut être chiffré** soit par une clé unique, soit individuellement pour chaque credential.

**Fonctionnalités principales :**

- Import/export via QR Code.

- Communication temps réel avec le verifier (WebSocket).

- Gestion des **revocations** et mise à jour des credentials.

## Points à pouvoir expliquer

1. **Pourquoi le stockage sécurisé est critique ?**
   
   - Protège l’identité numérique en cas de vol ou manipulation.
   
   - Évite que quelqu’un modifie ou lise les credentials directement.

2. **Comment le wallet fonctionne ?**
   
   - Contient les credentials chiffrés.
   
   - Permet des preuves cryptographiques (ZKP, BBS).
   
   - Facilite la révocation et le renouvellement.

3. **Les limitations actuelles :**
   
   - Identité liée à un smartphone → perte = perte de l’identité.
   
   - Fingerprinting côté navigateur peut réduire l’anonymat.
   
   - Besoin de bibliothèques cryptographiques manquantes en Kotlin.

4. **Solutions envisagées :**
   
   - **MPC** pour permettre transfert et backup sécurisé.
   
   - Service externe pour la gestion des clés et la révocation.

## 

C’est quoi le “wallet” ?

Dans ton projet E-ID, le **wallet** est une application mobile qui agit comme un **porte-feuille numérique d’identité** :

- Il **contient tes credentials**, c’est-à-dire toutes les preuves qui disent “Filipe Moreira a telle identité” (nom, date de naissance, adresse, etc.).

- Il permet de **les gérer** : ajouter, supprimer, partager seulement certaines informations.

- Il est contrôlé par l’utilisateur (toi, le Holder).

👉 Sans wallet, tu n’as aucun moyen de **garder tes identités numériques sous ton contrôle**.

---

## 2️⃣ C’est quoi le “stockage sécurisé” ?

Le **storage sécurisé** est l’endroit où le wallet **range en sécurité** les credentials :

- Sur un smartphone, tout peut être copié ou volé si ce n’est pas protégé.

- Le storage sécurisé garantit que **personne ne peut lire ou modifier les credentials** sauf le wallet lui-même.

- Sur Android, ça se fait via le **KeyStore** et le **chiffrement AES**, qui transforment les données en un format illisible sans la clé.

---

## 3️⃣ Le lien entre wallet et storage

On peut faire l’analogie suivante :

| Concept          | Exemple concret                                                                                                               |
| ---------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| Wallet           | C’est ton **porte-feuille** : il contient tes cartes d’identité (credentials)                                                 |
| Storage sécurisé | C’est le **coffre-fort à l’intérieur du porte-feuille** : personne ne peut ouvrir les cartes sauf toi, grâce à la clé secrète |

### En pratique :

1. Tu reçois un credential (ex. un QR code).

2. Le wallet le transforme en **JSON** et le **chiffre** avec une clé stockée dans le **KeyStore**.

3. Il le range dans le **storage sécurisé** du téléphone.

4. Quand tu veux montrer seulement une partie de ton credential à un vérifieur, le wallet :
   
   - Déchiffre le credential depuis le storage sécurisé,
   
   - Sélectionne les attributs à révéler (ex. nom + date de naissance),
   
   - Envoie uniquement ces informations.

💡 Donc le **storage sécurisé permet au wallet de protéger les données** pendant qu’il les gère et les partage.

---

## 4️⃣ Pourquoi c’est important ?

- Sans **wallet**, tu n’as pas de contrôle sur tes identités numériques.

- Sans **storage sécurisé**, n’importe qui pourrait copier tes credentials et les utiliser à ta place.

- Ensemble, **wallet + storage sécurisé** te donnent :
  
  - **Confidentialité** : seules les informations choisies sont partagées.
  
  - **Intégrité** : personne ne peut modifier tes credentials.
  
  - **Sécurité** : tes credentials sont protégés même si ton téléphone est volé.
