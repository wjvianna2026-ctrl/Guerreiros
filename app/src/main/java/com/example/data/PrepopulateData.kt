package com.example.data

object PrepopulateData {
    suspend fun populateDatabase(dao: ClubDao) {
        if (dao.getMemberCount() > 0) return

        // 1. Configurações Iniciais do Clube
        val settings = ClubSettingsEntity(
            id = 1,
            clubName = "Guerreiros do Bem Moto Clube",
            motto = "Honra, Respeito e Irmandade nas Estradas",
            cnpjOrRegister = "48.291.802/0001-94",
            pixKey = "financeiro@guerreirosdobem.mc.br",
            pixKeyType = "Chave E-mail",
            pixHolderName = "Guerreiros do Bem MC - Fundo Social",
            defaultMonthlyDue = 50.0,
            currentCashBalance = 0.0,
            rotatingFundBalance = 3500.0,
            cloudSyncStatus = "Sincronizado Localmente (Pronto para Nuvem Firestore)"
        )
        dao.insertSettings(settings)

        // 2. Irmãos / Integrantes
        val members = listOf(
            MemberEntity(
                id = 1,
                fullName = "Carlos Eduardo da Silva",
                roadNickname = "Trovão",
                role = "Presidente",
                phone = "11981234567",
                email = "trovao@guerreirosdobem.mc.br",
                pixKey = "11981234567",
                joinDate = "10/01/2018",
                status = "Ativo",
                notes = "Fundador do Moto Clube. Pilota uma Fat Boy 114."
            ),
            MemberEntity(
                id = 2,
                fullName = "Roberto Machado",
                roadNickname = "Machado",
                role = "Vice-Presidente",
                phone = "11982345678",
                email = "machado@guerreirosdobem.mc.br",
                pixKey = "machado.biker@gmail.com",
                joinDate = "15/05/2019",
                status = "Ativo",
                notes = "Responsável por rotas e eventos estradeiros."
            ),
            MemberEntity(
                id = 3,
                fullName = "Alexandre Pires",
                roadNickname = "Caveira",
                role = "Diretor Financeiro",
                phone = "11983456789",
                email = "caveira@guerreirosdobem.mc.br",
                pixKey = "caveira.contabil@gmail.com",
                joinDate = "20/08/2019",
                status = "Ativo",
                notes = "Gestor do fundo de apoio e controle de contas."
            ),
            MemberEntity(
                id = 4,
                fullName = "Marcelo Souza",
                roadNickname = "Graxa",
                role = "Tesoureiro",
                phone = "11984567890",
                email = "graxa@guerreirosdobem.mc.br",
                pixKey = "11984567890",
                joinDate = "05/02/2020",
                status = "Ativo",
                notes = "Mecânico oficial das viagens e cobranças amigáveis."
            ),
            MemberEntity(
                id = 5,
                fullName = "Julio Cesar Mendes",
                roadNickname = "Lobo Solitário",
                role = "Escudo Fechado",
                phone = "11985678901",
                email = "lobo@guerreirosdobem.mc.br",
                pixKey = "lobo.mendes@hotmail.com",
                joinDate = "12/11/2020",
                status = "Ativo",
                notes = "Pilota Shadow 750 preta fosca."
            ),
            MemberEntity(
                id = 6,
                fullName = "Fernando Costa",
                roadNickname = "Coruja",
                role = "Escudo Fechado",
                phone = "11986789012",
                email = "coruja@guerreirosdobem.mc.br",
                pixKey = "11986789012",
                joinDate = "08/07/2021",
                status = "Ativo",
                notes = "Batedor do pelotão de estrada."
            ),
            MemberEntity(
                id = 7,
                fullName = "Rodrigo Fagundes",
                roadNickname = "Guará",
                role = "Meio Escudo",
                phone = "11987890123",
                email = "guara@guerreirosdobem.mc.br",
                pixKey = "guara.fagundes@yahoo.com",
                joinDate = "14/03/2023",
                status = "Ativo",
                notes = "Em processo de validação de escudo completo."
            ),
            MemberEntity(
                id = 8,
                fullName = "Lucas Henrique Lima",
                roadNickname = "Fumaça",
                role = "Próspero",
                phone = "11988901234",
                email = "fumaca@guerreirosdobem.mc.br",
                pixKey = "11988901234",
                joinDate = "02/08/2024",
                status = "Ativo",
                notes = "Apoiador novato. Apaixonado por customização."
            )
        )
        dao.insertMembers(members)

        // 3. Mensalidades do Mês Corrente (09/2026) e Anterior (08/2026)
        val dues = listOf(
            MonthlyDueEntity(memberId = 1, memberName = "Carlos Eduardo da Silva", memberNickname = "Trovão", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Paga", paidDate = "08/09/2026", paymentMethod = "PIX", receiptNotes = "Tx #881290"),
            MonthlyDueEntity(memberId = 2, memberName = "Roberto Machado", memberNickname = "Machado", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Paga", paidDate = "09/09/2026", paymentMethod = "PIX", receiptNotes = "Tx #881340"),
            MonthlyDueEntity(memberId = 3, memberName = "Alexandre Pires", memberNickname = "Caveira", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Paga", paidDate = "05/09/2026", paymentMethod = "PIX", receiptNotes = "Tx #881001"),
            MonthlyDueEntity(memberId = 4, memberName = "Marcelo Souza", memberNickname = "Graxa", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Paga", paidDate = "10/09/2026", paymentMethod = "Dinheiro", receiptNotes = "Recebido na sede"),
            MonthlyDueEntity(memberId = 5, memberName = "Julio Cesar Mendes", memberNickname = "Lobo Solitário", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Pendente", paidDate = null, paymentMethod = null, receiptNotes = null),
            MonthlyDueEntity(memberId = 6, memberName = "Fernando Costa", memberNickname = "Coruja", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Paga", paidDate = "07/09/2026", paymentMethod = "PIX", receiptNotes = "Tx #881145"),
            MonthlyDueEntity(memberId = 7, memberName = "Rodrigo Fagundes", memberNickname = "Guará", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Pendente", paidDate = null, paymentMethod = null, receiptNotes = null),
            MonthlyDueEntity(memberId = 8, memberName = "Lucas Henrique Lima", memberNickname = "Fumaça", monthYear = "09/2026", amount = 50.0, dueDate = "10/09/2026", status = "Em Atraso", paidDate = null, paymentMethod = null, receiptNotes = null)
        )
        dao.insertMonthlyDues(dues)

        // 4. Fundo Rotativo - Empréstimo com juros sociais
        val loan = LoanRequestEntity(
            id = 1,
            memberId = 5,
            memberName = "Julio Cesar Mendes",
            memberNickname = "Lobo Solitário",
            totalAmount = 1200.0,
            monthlyInterestRate = 1.0, // 1% ao mês juros social
            installmentsCount = 4,
            purpose = "Conserto de embreagem e kit relação da Shadow 750",
            requestDate = "15/07/2026",
            firstDueDate = "15/08/2026",
            status = "Ativo",
            totalInterest = 48.0,
            totalPayable = 1248.0,
            notes = "Apoiado em reunião unânime da diretoria."
        )
        dao.insertLoan(loan)

        val loanInstallments = listOf(
            LoanInstallmentEntity(loanId = 1, memberId = 5, memberNickname = "Lobo Solitário", installmentNumber = 1, dueDate = "15/08/2026", principalAmount = 300.0, interestAmount = 12.0, totalAmount = 312.0, status = "Paga", paidDate = "14/08/2026", paymentMethod = "PIX"),
            LoanInstallmentEntity(loanId = 1, memberId = 5, memberNickname = "Lobo Solitário", installmentNumber = 2, dueDate = "15/09/2026", principalAmount = 300.0, interestAmount = 12.0, totalAmount = 312.0, status = "Pendente", paidDate = null, paymentMethod = null),
            LoanInstallmentEntity(loanId = 1, memberId = 5, memberNickname = "Lobo Solitário", installmentNumber = 3, dueDate = "15/10/2026", principalAmount = 300.0, interestAmount = 12.0, totalAmount = 312.0, status = "Pendente", paidDate = null, paymentMethod = null),
            LoanInstallmentEntity(loanId = 1, memberId = 5, memberNickname = "Lobo Solitário", installmentNumber = 4, dueDate = "15/11/2026", principalAmount = 300.0, interestAmount = 12.0, totalAmount = 312.0, status = "Pendente", paidDate = null, paymentMethod = null)
        )
        dao.insertLoanInstallments(loanInstallments)

        // 5. Compras Coletivas
        val purchase = CollectivePurchaseEntity(
            id = 1,
            title = "Coletes de Couro e Brasões Bordados GDB-MC",
            supplier = "Couros Estrada Velha & Cia",
            totalCost = 1600.0,
            installmentsCount = 2,
            purchaseDate = "01/08/2026",
            firstDueDate = "20/08/2026",
            targetType = "Rateio Geral",
            assignedMemberNames = "Guará, Fumaça, Coruja, Lobo Solitário",
            status = "Ativo",
            notes = "Aquisição de 4 novos coletes oficiais para os irmãos recém escudados e prósperos."
        )
        dao.insertPurchase(purchase)

        val quotas = listOf(
            PurchaseQuotaEntity(purchaseId = 1, purchaseTitle = "Coletes de Couro Oficiais", memberId = 7, memberName = "Rodrigo Fagundes", memberNickname = "Guará", installmentNumber = 1, dueDate = "20/08/2026", amount = 200.0, status = "Paga", paidDate = "19/08/2026"),
            PurchaseQuotaEntity(purchaseId = 1, purchaseTitle = "Coletes de Couro Oficiais", memberId = 7, memberName = "Rodrigo Fagundes", memberNickname = "Guará", installmentNumber = 2, dueDate = "20/09/2026", amount = 200.0, status = "Pendente", paidDate = null),
            PurchaseQuotaEntity(purchaseId = 1, purchaseTitle = "Coletes de Couro Oficiais", memberId = 8, memberName = "Lucas Henrique Lima", memberNickname = "Fumaça", installmentNumber = 1, dueDate = "20/08/2026", amount = 200.0, status = "Paga", paidDate = "20/08/2026"),
            PurchaseQuotaEntity(purchaseId = 1, purchaseTitle = "Coletes de Couro Oficiais", memberId = 8, memberName = "Lucas Henrique Lima", memberNickname = "Fumaça", installmentNumber = 2, dueDate = "20/09/2026", amount = 200.0, status = "Pendente", paidDate = null)
        )
        dao.insertPurchaseQuotas(quotas)

        // 6. Auditoria / Livro Caixa Inicial
        val transactions = listOf(
            CashTransactionEntity(type = "Entrada", category = "Saldo Inicial", amount = 5000.0, description = "Aporte Inicial de Caixa - Fundo Social dos Fundadores", memberName = "Guerreiros do Bem MC", date = "01/07/2026 08:00", operator = "Diretoria Fundadora"),
            CashTransactionEntity(type = "Entrada", category = "Mensalidade", amount = 50.0, description = "Mensalidade 09/2026 - Trovão", memberName = "Trovão", date = "08/09/2026 14:22", operator = "Diretoria Financeira"),
            CashTransactionEntity(type = "Entrada", category = "Mensalidade", amount = 50.0, description = "Mensalidade 09/2026 - Machado", memberName = "Machado", date = "09/09/2026 10:15", operator = "Tesoureiro"),
            CashTransactionEntity(type = "Entrada", category = "Parcela Empréstimo", amount = 312.0, description = "Parcela 01/04 Empréstimo Shadow 750 (R$ 300 principal + R$ 12 juro)", memberName = "Lobo Solitário", date = "14/08/2026 16:30", operator = "Diretoria Financeira"),
            CashTransactionEntity(type = "Saída", category = "Fundo Rotativo", amount = 1200.0, description = "Liberação Empréstimo Social - Peças e Retífica Shadow 750", memberName = "Lobo Solitário", date = "15/07/2026 11:00", operator = "Presidente / Dir. Financeiro"),
            CashTransactionEntity(type = "Entrada", category = "Rateio Compra", amount = 200.0, description = "Parcela 1 Colete de Couro Oficial - Guará", memberName = "Guará", date = "19/08/2026 09:40", operator = "Tesoureiro"),
            CashTransactionEntity(type = "Entrada", category = "Rateio Compra", amount = 200.0, description = "Parcela 1 Colete de Couro Oficial - Fumaça", memberName = "Fumaça", date = "20/08/2026 18:05", operator = "Tesoureiro")
        )
        for (tx in transactions) {
            dao.insertTransaction(tx)
        }

        val txs = dao.getAllTransactionsList()
        val initBalance = txs.filter { it.type == "Entrada" }.sumOf { it.amount } - txs.filter { it.type == "Saída" }.sumOf { it.amount }
        dao.setCashBalance(initBalance)
    }
}
