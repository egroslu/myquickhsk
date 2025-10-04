- [Project Description](#project-description)  
- [Foreword](#foreword)  
- [Versions](#versions)  
- [Platform Information](#platform-information)  
- [Front / Back Support](#frontback-support)  
- [Present Functionality](#present-functionality)  
- [Design](#design)
- [Credits & License](#credits--license)

# 📄 我的 快速 HSK / MyQuickHSK (_README / WIKI_)
### _HAND-SELECTED, HSK LEVEL-VARIABLE, CUSTOMIZABLE STUDY CARD DIGITAL SERVICE_

## PROJECT DESCRIPTION

This Android application provides Mandarin learners with an offline, self-tailorable review and dictionary service.  Downloaded locally, onto their device, comes a searchable database with 100,000+ character entries (which is especially handy when needing access to characters without phone service).  Users can select their HSK level and then, at a minimum, customize review vocabulary via easy-to-use flipcards.  And the app integrates a local Room (Android) database, on-device, ensuring the user's preferred state persistence.

## FOREWORD

Many learners struggle to access structured Mandarin vocabulary self-tailored to their level; existing apps are often subscription-based and/or lack [any] customization.  This project provides a free, simple, customizable alternative where learners can study HSK-aligned vocabulary and expand their deck with personal notes.  This project is built with inspiration from the developer's own, personal journey into learning Mandarin and having issues with Quality of Life (QoL) features across different language learning apps.  Though this is not a commercial product, it is a project satisfying not only banal requirements for a University semester project as an illustrative learning example, but, also, the developer's own vestedly-interested desires concerning their language-learning.  Future versions will, undoubtedly, continually interate moving forward; as, the project aims for top marks both academic _and_ serviceable in the Mobile Application market; so, why not provide the service, indefinitely, adding features as able?

## VERSIONS
The first iteration of this service aims to provide a TTS-centric (Text-to-Speech), litely-gamified personalized review and notation service coupled with quick-access to what is often considered the gold standard of Chinese character collections (CC-CEDICT).  Future versions need center around phrasebuilding; grammar and context learning; audiovisual samples and reviews; AI- and live-partner-speech practice; and beyond.

#

### <ins>VERSION 1.0 FEATURES</ins>
-  _Hand-Chosen HSK Level Character Sets (full HSK 1-9)_
-  _TTS (Text-to-Speech) Dictionary (full 100K+ language set)_
-  _Review Cards (w/ optional Favorites & Unpracticed modes)_
-  _Digital Notebook (per-card; in-app)_
-  _UI Theme Customization  (Light / Dark; Theme Color Variants)_
-  _100% Offline General Access_

## PLATFORM INFORMATION
### ANDROID

IDE: _Android Studio_ (latest stable release)

Language: _Java_

### FRONT/BACK SUPPORT

Front-End (User-Facing):
-  Activities for menu, flashcards, and search
-  XML layouts for UI (RecyclerView for search results list)

Back-End (Data-Handling):
-  Room database for local storage, persistence, and CRUD operations

## PRESENT FUNCTIONALITY
-  _HSK Level Selection_: Choose between HSK 1-9 lists.
-  _Search_: Look up words across all levels via dictionary.
-  _TTS (Text-to-Speech)_: Hear pronunciation using Android’s TextToSpeech service.
-  _Flashcard Review_: Browse and flip study cards.
-  _Select Favorites_: Save and notate personal flashcards.
-  _Persistence_: Dictionary, reviewed terms, favorites, and notes locally saved via built-in Android Resources (res) and via Room DB.

## DESIGN

![An image](./wireframe.png)
(<ins>**NOTE**</ins> — _Wireframe presented in simple schematic format to highlight flow and functionality. Final app layout varies._)

## CHANGELOG

### Version 0.5 (Week 6)
- Removed "Bests" concept (not conducive to UX and functionally unuseful)
- Added Unpracticed term filter in-place of removed "Bests" system
- Moved full language dictionary to local storage (negligble to ship locally)

### Version 0.4 (Week 5)
- Removed "Unlocks" concept (not conducive to UX and functionally unuseful)
- Chose to host HSK lists local to the app storage (negligble to ship locally)

### Version 0.3 (Week 4)
- Consolidated Favorites feature into dictionary (simplified scope)
- Finalized splashes and RecyclerView with search/filter shell (HSK levels, Favorites switch, [un]practiced filters)
- Began work on CC-CEDICT parser
- Prepared for Room DB integration for persistence of user data

### Version 0.2 (Week 3)
- Implemented navigation between splash screens and placeholder Activities
- Added Disclaimer (Release of Claims) to README/Wiki

### Version 0.1 (Week 1)
- Defined project idea and scope.
- Outlined Minimum Viable Product ("MVP"): Flashcards + dictionary (w/ HSK level filtering)
- Crafted wireframes for core activities (splashes, review, dictionary)
- Outlined Version 1.0 features list and overall scope
- Repo initialized with README / Wiki

## CREDITS & LICENSE
### _Credits_:
Built by _Ethan G._ for SAINT LEO UNIVERSITY, COM-437-OL01 — "Mobile Application Development" graded homework project [FALL 2025 course section]
### _Data Sources_:
- CC-CEDICT (public domain)
- Curated HSK lists (_Ethan G._ via open educational resources)
- Native emojis (Android builtins)

## DISCLAIMER (RELEASE OF CLAIMS)
This project — including all code, design, documentation, and related intellectual property — is created and privately owned solely by the author _Ethan G._

SAINT LEO UNIVERSITY, its faculty, staff (including adjunct), and affiliates are expressly disclaimed from any ownership, rights, and all other forms of claims to the project (whether arising under course participation, instruction, or otherwise).  Submission for academic purposes does not confer, transfer, or assign any portion of ownership or interest to SAINT LEO UNIVERSITY.

Any attempts to claim any of the above by SAINT LEO UNIVERSITY or its representatives will be pursued under the full faith and letter of Florida State and/or US Federal law.

By accepting this project for graded marks, SAINT LEO UNIVERSITY waives all supposed rights to the project and, in the event of a breach of this agreement, SAINT LEO UNIVERSITY agrees to foot all related legal costs of both parties for the full course of the resulting arbitration.

