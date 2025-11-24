### Composants principaux

1. **MainActivity**  
   - Coordonne l’ensemble du flux de chiffrement et stockage.
   - Génère un **DEK** (clé de chiffrement de données).
   - Interagit avec le **KMS simulé** pour obtenir un **KEK**.
   - Chiffre les credentials et encapsule la DEK avec le KEK.
   - Stocke les données chiffrées sur le stockage interne.

2. **KmsSimulator**  
   - Simule un **Key Management System**.
   - Génère des KEK (AES 256 bits).
   - Permet de "wrap/unwrap" des DEK via AESWrap.

3. **CryptoUtils**  
   - Fournit des fonctions utilitaires de chiffrement et déchiffrement AES avec IV.
   - Sépare la logique cryptographique du flux principal.

---

## Flux de données sécurisé

1. **Demande de KEK au KMS**  
   Le KMS renvoie une clé symétrique unique (KEK) avec un identifiant `keyId`.

2. **Génération de la DEK**  
   Une DEK est générée localement pour chiffrer les credentials.  
   Dans la version actuelle, la DEK est **exportable** et en mémoire pour simplification.

3. **Chiffrement des credentials**  
   Les credentials (`user:password`) sont chiffrés avec la DEK.  
   Le chiffrement utilise **AES/CBC/PKCS7Padding** avec un vecteur d’initialisation (IV).

4. **Encapsulation de la DEK**  
   La DEK est encapsulée avec le KEK via **AESWrap**.  
   Cela garantit que seule la KEK du KMS peut déchiffrer la DEK.

5. **Stockage sécurisé**  
   Les données suivantes sont stockées dans un fichier interne Android (`holder_data.enc`) :
   - IV + données chiffrées
   - `keyId` du KEK
   - DEK encapsulée (wrappedKey)

6. **Déchiffrement pour vérification**  
   Les données stockées sont lues depuis le fichier interne.  
   La DEK est utilisée pour déchiffrer les credentials et vérifier la validité du processus.

---

## Points clés de sécurité

- **Séparation des clés** : La DEK est utilisée pour chiffrer les données, et la KEK pour protéger la DEK.  
- **AES 256 bits** : Utilisation de chiffrement symétrique fort.  
- **IV aléatoire** pour chaque chiffrement, garantissant un chiffrement non déterministe.  
- **Stockage interne sécurisé** : Les données chiffrées et clés encapsulées sont stockées dans un répertoire accessible uniquement à l’application.  
- **KMS simulé** : Permet de démontrer la logique de key wrapping sans exposer de KEK directement.

---

## Technologies utilisées

- Kotlin  
- Android SDK  
- Android KeyStore (optionnel dans versions précédentes)  
- Bouncy Castle (via CryptoUtils et Cipher AESWrap)  
- Base64 pour sérialisation de clés et données chiffrées

---
