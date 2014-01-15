
<ul>
	<li> Relatórios
		<ul>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEditalTotalAlunos}" value="Total de Alunos Envolvidos" onclick="setAba('extensao')"/></li>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEditalBolsasSolicitadas}" value="Total de Bolsas Solicitadas" onclick="setAba('extensao')"/></li>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioOrcamentoSolicitado}" value="Total de Recursos Solicitados (Orçamento)" onclick="setAba('extensao')"/></li>																						
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de Público Estimado das Ações" onclick="setAba('extensao')"/></li>
            <li><h:commandLink action="#{relatoriosPlanejamento.iniciarRENEX}" value="Emitir relatório RENEX" onclick="setAba('extensao')" /></li>
			<li><a href="${ctx}/extensao/Relatorios/dados_bancarios_discentes_form.jsf?aba=extensao">Dados Bancários de Discentes de Extensão</a></li>
			<li><h:commandLink action="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.iniciar}" value="Relatório de Alunos em Atividades de Extensão, Monitoria e Pesquisa" onclick="setAba('extensao')" /></li>
		</ul>
	</li>	
	<li> Ações de Extensão
        <ul>
            <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar ações" onclick="setAba('extensao')" /></li>
			<li><a href="${ctx}/extensao/DiscenteExtensao/busca_discente.jsf?aba=extensao">Localizar Discentes de Extensão</a></li>
            <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Consultar Andamento das Avaliações" onclick="setAba('extensao')" /></li>
            <li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}" value="Autorizar Propostas de Ações" onclick="setAba('extensao')" /></li>
            <li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}" value="Autorizar Relatórios Parciais e Finais" onclick="setAba('extensao')" /></li>
		</ul>
		
	</li>
	<li>Relatórios Quantitativos
	<ul>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoParticipantes}" value="Total de Ações e Participantes Ativos por Área Temática" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioTotalAcaoEdital.iniciarRelatorioAcoesEdital}" value=" Total de Ações de Extensão que Concorreram a Editais Públicos" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorVinculoENivel}" value="Total de Discentes Ativos por Nível e Vínculo" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorNivel}" value="Total de Discentes Ativos por Nível de Ensino" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioPlanosTrabalho}" value="Total de Discentes com Planos de Trabalho" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalDiscentesParticipantesPlanoTrabalhoExtensao}" value="Total de Discentes com Planos de Trabalho Participantes de Ações de Extensão" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscenteNaEquipe}" value="Total de Discentes como membros da equipe" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesProjetoDiscentes}" value="Total de Discentes das Equipes dos Projetos Participantes de Ações de Extensão" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesDocentesExtensao}" value="Total de Docentes Participantes de Ações de Extensão" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesPorNivel}" value="Total de Docentes por Nível" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesDetalhado}" value="Total de Docentes por Tipo de Ação" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioExternoDetalhado}" value="Total de Participantes Externos por Tipo de Ação" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoProdutos}" value="Total de Produtos Ativos por Área Temática" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPublicoAtingido.iniciarRelatorioPublicoAtingido}" value="Total de Público Atingido com Base nos Relatórios Submetidos" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de Público Estimado x Público Atingido" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioTecnicoAdmDetalhado}" value="Total de Técnicos Admin. por Tipo de Ação" onclick="setAba('extensao')"/></li>
	</ul>
	</li>	
</ul>