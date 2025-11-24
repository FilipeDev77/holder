# Secure Holder

## Description

**Secure Holder** est une application Android démontrant un flux sécurisé de gestion et de chiffrement de **credentials** (identifiants utilisateur). Le projet illustre les concepts de **cryptographie symétrique**, **encapsulation de clés (key wrapping)**, et **stockage sécurisé** sur un appareil Android, avec une simulation d’un **KMS (Key Management System)**.

L’objectif est de montrer comment stocker des données sensibles de manière sécurisée tout en utilisant un **Key Encryption Key (KEK)** et un **Data Encryption Key (DEK)**.

### Composants principaux

1. **MainActivity**
    - Coordonne l’ensemble du flux de chiffrement et stockage.
    - Génère un **DEK** (clé de chiffrement de données) localement.
    - Interagit avec le **KMS simulé** pour obtenir un **keyId** associé au KEK.
    - Chiffre les credentials et demande au KMS d’encapsuler la DEK avec la KEK.
    - Stocke les données chiffrées sur le stockage interne.

2. **KmsSimulator**
    - Simule un **Key Management System**.
    - Génère des KEK (AES 256 bits) côté serveur.
    - Retourne uniquement le **keyId** au Holder pour la référence.
    - Permet de "wrap/unwrap" des DEK via AESWrap lors de l’échange sécurisé avec le Holder.

3. **CryptoUtils**
    - Fournit des fonctions utilitaires de chiffrement et déchiffrement AES avec IV.
    - Sépare la logique cryptographique du flux principal.

---

## Flux de données sécurisé

1. **Demande de KEK au KMS**  
   Le Holder demande au KMS une clé KEK.  
   - KMS génère le KEK.  
   - **Seul le `keyId` est renvoyé** au Holder (la KEK reste côté KMS).

2. **Génération de la DEK locale**  
   Le Holder génère une DEK AES 256 bits en mémoire pour chiffrer les credentials.  
   - La DEK est locale et connue uniquement du Holder à ce stade.  
   - Elle permet au Holder de **chiffrer immédiatement les credentials**.

3. **Chiffrement des credentials**  
   Les credentials (`user:password`) sont chiffrés avec la DEK locale.  
   - Utilisation de **AES/CBC/PKCS7Padding** avec un IV aléatoire pour chaque chiffrement.

4. **Encapsulation (wrap) de la DEK via le KMS**  
   - Le Holder envoie au KMS : le `keyId` et la DEK générée.  
   - KMS utilise le KEK correspondant pour encapsuler (`wrap`) la DEK.  
   - Le KMS renvoie la DEK encapsulée au Holder.  

5. **Stockage sécurisé**  
   Les éléments suivants sont stockés dans un fichier interne Android (`holder_data.enc`) :
    - IV + données chiffrées
    - `keyId` du KEK
    - DEK encapsulée (`wrappedKey`)  

6. **Déchiffrement des credentials**  
   - Pour relire les credentials, le Holder envoie au KMS le `wrappedKey` et le `keyId`.  
   - KMS **unwrap** la DEK et la renvoie au Holder.  
   - Le Holder utilise la DEK récupérée pour déchiffrer les credentials.

---

## Points clés de sécurité

- **Séparation des clés** : La DEK est utilisée pour chiffrer les données, et la KEK pour protéger la DEK.
- **AES 256 bits** : Chiffrement symétrique fort pour DEK et KEK.
- **IV aléatoire** pour chaque chiffrement, garantissant un chiffrement non déterministe.
- **Stockage interne sécurisé** : Les données chiffrées et clés encapsulées sont stockées dans un répertoire accessible uniquement à l’application.
- **KMS simulé** : Permet de démontrer la logique de key wrapping et unwrap sans exposer le KEK directement.
- **DEK locale** : Permet au Holder de chiffrer ses données immédiatement avant le wrapping.

---

## Technologies utilisées

- Kotlin
- Android SDK
- Android KeyStore (optionnel dans certaines versions)
- Bouncy Castle (via CryptoUtils et Cipher AESWrap)
- Base64 pour sérialisation de clés et données chiffrées
