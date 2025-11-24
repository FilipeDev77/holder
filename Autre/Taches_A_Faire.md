# ✅ Checklist simplifiée pour le POC sans MPC

### 1️⃣ KeyStore local

- Générer une **clé AES hardware-backed** dans Android Keystore (`local-storage-key`).

- Vérifier que la clé existe avant génération.

- Tester chiffrement/déchiffrement avec cette clé.

### 2️⃣ Chiffrement local des credentials

- Créer un fichier `kms_credentials.enc`.

- Chiffrer les credentials KMS avec la clé du Keystore.

- Vérifier la lecture et le déchiffrement correct du fichier.

### 3️⃣ Déchiffrement et usage en mémoire

- Charger les credentials depuis le fichier chiffré.

- Déchiffrer uniquement en RAM.

- Effacer la mémoire après usage (`wipe()`).

### 4️⃣ Gestion de la session / déconnexion

- Supprimer les fichiers chiffrés lors de la déconnexion.

- Supprimer la clé Keystore si tu veux sécurité maximale.

- Régénérer la clé AES au prochain login si nécessaire.

### 5️⃣ Sécurité

- Ne jamais stocker la clé privée KMS sur le téléphone.

- Limiter la durée de vie des secrets en RAM.

- TLS + authentification forte pour tout échange backend/KMS.

- Overwrite des fichiers sensibles avant suppression.

### 6️⃣ Test complet

- Génération clé AES → chiffrement credentials → stockage.

- Déchiffrement local → utilisation → wipe mémoire.

- Déconnexion → suppression clé + fichiers → nouveau login.

- Vérifier que tout fonctionne sans fragmentation de clé.

# 🏷️ Epic

**Epic : Gestion sécurisée des credentials KMS sur l’app mobile**

> En tant qu’application mobile,  
> je veux stocker et utiliser des credentials KMS de manière sécurisée sur le téléphone,  
> afin de pouvoir déchiffrer les données locales et interagir avec le backend sans exposer les secrets.

---

# 📌 User Stories

### **US1 : Génération de la clé AES locale**

**En tant qu’application**,  
je veux générer une clé AES hardware-backed dans le KeyStore,  
afin de pouvoir chiffrer et déchiffrer les credentials KMS localement.

**Critères d’acceptation :**

- La clé est créée une seule fois par installation.

- La clé reste dans le Keystore et n’est jamais exportable.

- La clé peut être utilisée pour chiffrer et déchiffrer des fichiers.

---

### **US2 : Chiffrement des credentials KMS**

**En tant qu’application**,  
je veux chiffrer les credentials KMS avec la clé AES du KeyStore,  
afin de pouvoir les stocker sur le téléphone sans risque.

**Critères d’acceptation :**

- Le fichier `kms_credentials.enc` est créé sur le disque interne.

- Les credentials ne sont jamais stockés en clair sur le téléphone.

- Le chiffrement utilise AES-GCM avec IV unique.

---

### **US3 : Déchiffrement en mémoire**

**En tant qu’application**,  
je veux déchiffrer les credentials depuis le fichier chiffré uniquement en RAM,  
afin de pouvoir les utiliser temporairement pour appeler le backend ou déchiffrer des données.

**Critères d’acceptation :**

- Les credentials sont déchiffrés uniquement en RAM.

- Après usage, les credentials sont effacés de la mémoire.

- Le fichier sur disque reste chiffré.

---

### **US4 : Gestion de la session / déconnexion**

**En tant qu’utilisateur**,  
je veux que mes credentials locaux soient supprimés à la déconnexion,  
afin de protéger mes données si quelqu’un accède au téléphone.

**Critères d’acceptation :**

- Les fichiers chiffrés sont effacés et surécrits.

- La clé AES peut être supprimée du Keystore si nécessaire.

- Au prochain login, une nouvelle clé AES peut être générée.

---

### **US5 : Sécurité et bonnes pratiques**

**En tant qu’équipe de développement**,  
je veux respecter les meilleures pratiques de sécurité mobile,  
afin de protéger les secrets KMS et éviter toute fuite.

**Critères d’acceptation :**

- La clé privée KMS n’est jamais stockée sur l’appareil.

- Les communications backend/KMS sont sécurisées via TLS et authentification forte.

- Les fichiers sensibles sont effacés correctement.

- La durée de vie des secrets en RAM est minimale.

---

### **US6 : Test et validation**

**En tant qu’équipe QA**,  
je veux tester le cycle complet de chiffrement/déchiffrement et de suppression,  
afin de m’assurer que le système est sécurisé et fonctionne correctement.

**Critères d’acceptation :**

- Génération clé AES → chiffrement → stockage → déchiffrement → utilisation → wipe mémoire fonctionne sans erreur.

- Déconnexion supprime fichiers et clé si configuré.

- L’application reste fonctionnelle après régénération de la clé.
