package com.example.data

data class ClubPatente(
    val title: String,
    val familyName: String,
    val level: Int, // 1 a 5
    val levelLabel: String,
    val description: String,
    val shortBadge: String
)

data class PatenteFamily(
    val id: String,
    val name: String,
    val shortName: String,
    val description: String,
    val patentes: List<ClubPatente>
)

object ClubPatentesRegistry {
    val families: List<PatenteFamily> = listOf(
        PatenteFamily(
            id = "diretoria",
            name = "Diretoria Executiva",
            shortName = "Diretoria",
            description = "Liderança institucional e gestão estratégica do Moto Clube",
            patentes = listOf(
                ClubPatente(
                    title = "Presidente",
                    familyName = "Diretoria Executiva",
                    level = 1,
                    levelLabel = "1º Nível Hierárquico",
                    description = "Comando supremo do Moto Clube, representação legal e institucional.",
                    shortBadge = "1º Nível • Liderança"
                ),
                ClubPatente(
                    title = "Vice-Presidente",
                    familyName = "Diretoria Executiva",
                    level = 2,
                    levelLabel = "2º Nível Hierárquico",
                    description = "Subcomando imediato à Presidência e coordenação geral de projetos.",
                    shortBadge = "2º Nível • Subcomando"
                ),
                ClubPatente(
                    title = "Diretor Financeiro",
                    familyName = "Diretoria Executiva",
                    level = 3,
                    levelLabel = "3º Nível Hierárquico",
                    description = "Gestão financeira, fluxo de caixa, mensalidades, empréstimos e cotas.",
                    shortBadge = "3º Nível • Tesouraria"
                ),
                ClubPatente(
                    title = "Diretor de Eventos",
                    familyName = "Diretoria Executiva",
                    level = 4,
                    levelLabel = "4º Nível Hierárquico",
                    description = "Coordenação de passeios oficiais, viagens, eventos sociais e sedes.",
                    shortBadge = "4º Nível • Eventos"
                ),
                ClubPatente(
                    title = "Secretário Geral",
                    familyName = "Diretoria Executiva",
                    level = 5,
                    levelLabel = "5º Nível Hierárquico",
                    description = "Registro de atas, cadastro dos integrantes e comunicações oficiais.",
                    shortBadge = "5º Nível • Secretaria"
                )
            )
        ),
        PatenteFamily(
            id = "estrada_disciplina",
            name = "Comando de Estrada & Disciplina",
            shortName = "Estrada & Disciplina",
            description = "Operacional de asfalto, segurança do comboio e honra do colete",
            patentes = listOf(
                ClubPatente(
                    title = "Capitão de Estrada",
                    familyName = "Comando de Estrada & Disciplina",
                    level = 1,
                    levelLabel = "1º Nível Hierárquico",
                    description = "Líder de pilotagem nos comboios, definição de velocidades e paradas.",
                    shortBadge = "1º Nível • Capitania"
                ),
                ClubPatente(
                    title = "Sargento de Armas",
                    familyName = "Comando de Estrada & Disciplina",
                    level = 2,
                    levelLabel = "2º Nível Hierárquico",
                    description = "Guardião da disciplina interna, respeito às normas e segurança.",
                    shortBadge = "2º Nível • Disciplina"
                ),
                ClubPatente(
                    title = "Sub-Capitão de Estrada",
                    familyName = "Comando de Estrada & Disciplina",
                    level = 3,
                    levelLabel = "3º Nível Hierárquico",
                    description = "Fechador de comboio (ferrolho), apoio tático e socorro mecânico.",
                    shortBadge = "3º Nível • Sub-Capitania"
                ),
                ClubPatente(
                    title = "Escudo Fechado",
                    familyName = "Comando de Estrada & Disciplina",
                    level = 4,
                    levelLabel = "4º Nível Hierárquico",
                    description = "Membro titular pleno com brasão completo e direito a voto no conselho.",
                    shortBadge = "4º Nível • Efetivo"
                ),
                ClubPatente(
                    title = "Meio Escudo",
                    familyName = "Comando de Estrada & Disciplina",
                    level = 5,
                    levelLabel = "5º Nível Hierárquico",
                    description = "Irmão em fase intermediária de estrada portando meio brasão.",
                    shortBadge = "5º Nível • Transição"
                )
            )
        ),
        PatenteFamily(
            id = "graduacao_ingresso",
            name = "Graduação & Novos Integrantes",
            shortName = "Graduação & Ingresso",
            description = "Formação de estrada, iniciação na irmandade e membros honorários",
            patentes = listOf(
                ClubPatente(
                    title = "Próspero",
                    familyName = "Graduação & Novos Integrantes",
                    level = 1,
                    levelLabel = "1º Nível Hierárquico",
                    description = "Membro probacionário (Prospect) em formação direta para receber as cores.",
                    shortBadge = "1º Nível • Prospect"
                ),
                ClubPatente(
                    title = "Postulante",
                    familyName = "Graduação & Novos Integrantes",
                    level = 2,
                    levelLabel = "2º Nível Hierárquico",
                    description = "Integrante em período de observação, convivência na sede e avaliação.",
                    shortBadge = "2º Nível • Avaliação"
                ),
                ClubPatente(
                    title = "Aspirante",
                    familyName = "Graduação & Novos Integrantes",
                    level = 3,
                    levelLabel = "3º Nível Hierárquico",
                    description = "Iniciante aprendendo os códigos de conduta, respeito e irmandade do MC.",
                    shortBadge = "3º Nível • Iniciação"
                ),
                ClubPatente(
                    title = "Apoio Oficial",
                    familyName = "Graduação & Novos Integrantes",
                    level = 4,
                    levelLabel = "4º Nível Hierárquico",
                    description = "Amigo e voluntário autorizado a rodar com o clube e apoiar ações.",
                    shortBadge = "4º Nível • Apoio"
                ),
                ClubPatente(
                    title = "Membro Benemérito",
                    familyName = "Graduação & Novos Integrantes",
                    level = 5,
                    levelLabel = "5º Nível Hierárquico",
                    description = "Irmão honorário com distinção histórica outorgada pela Diretoria.",
                    shortBadge = "5º Nível • Benemérito"
                )
            )
        )
    )

    val allPatentes: List<ClubPatente> = families.flatMap { it.patentes }
    val allTitles: List<String> = allPatentes.map { it.title }

    fun findPatente(title: String?): ClubPatente? {
        if (title.isNullOrBlank()) return null
        val clean = title.trim()
        return allPatentes.firstOrNull { it.title.equals(clean, ignoreCase = true) }
            ?: when {
                clean.contains("Tesoureiro", ignoreCase = true) -> allPatentes.firstOrNull { it.title == "Diretor Financeiro" }
                clean.contains("Escudo Fechado", ignoreCase = true) -> allPatentes.firstOrNull { it.title == "Escudo Fechado" }
                clean.contains("Meio Escudo", ignoreCase = true) -> allPatentes.firstOrNull { it.title == "Meio Escudo" }
                clean.contains("Próspero", ignoreCase = true) || clean.contains("Prospero", ignoreCase = true) -> allPatentes.firstOrNull { it.title == "Próspero" }
                else -> null
            }
    }
}
