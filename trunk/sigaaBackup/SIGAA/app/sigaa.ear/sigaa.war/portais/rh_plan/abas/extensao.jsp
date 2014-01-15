
<ul>
	<li> Relat�rios
		<ul>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEditalTotalAlunos}" value="Total de Alunos Envolvidos" onclick="setAba('extensao')"/></li>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEditalBolsasSolicitadas}" value="Total de Bolsas Solicitadas" onclick="setAba('extensao')"/></li>
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioOrcamentoSolicitado}" value="Total de Recursos Solicitados (Or�amento)" onclick="setAba('extensao')"/></li>																						
			<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de P�blico Estimado das A��es" onclick="setAba('extensao')"/></li>
            <li><h:commandLink action="#{relatoriosPlanejamento.iniciarRENEX}" value="Emitir relat�rio RENEX" onclick="setAba('extensao')" /></li>
			<li><a href="${ctx}/extensao/Relatorios/dados_bancarios_discentes_form.jsf?aba=extensao">Dados Banc�rios de Discentes de Extens�o</a></li>
			<li><h:commandLink action="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.iniciar}" value="Relat�rio de Alunos em Atividades de Extens�o, Monitoria e Pesquisa" onclick="setAba('extensao')" /></li>
		</ul>
	</li>	
	<li> A��es de Extens�o
        <ul>
            <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar a��es" onclick="setAba('extensao')" /></li>
			<li><a href="${ctx}/extensao/DiscenteExtensao/busca_discente.jsf?aba=extensao">Localizar Discentes de Extens�o</a></li>
            <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Consultar Andamento das Avalia��es" onclick="setAba('extensao')" /></li>
            <li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}" value="Autorizar Propostas de A��es" onclick="setAba('extensao')" /></li>
            <li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}" value="Autorizar Relat�rios Parciais e Finais" onclick="setAba('extensao')" /></li>
		</ul>
		
	</li>
	<li>Relat�rios Quantitativos
	<ul>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoParticipantes}" value="Total de A��es e Participantes Ativos por �rea Tem�tica" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioTotalAcaoEdital.iniciarRelatorioAcoesEdital}" value=" Total de A��es de Extens�o que Concorreram a Editais P�blicos" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorVinculoENivel}" value="Total de Discentes Ativos por N�vel e V�nculo" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorNivel}" value="Total de Discentes Ativos por N�vel de Ensino" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioPlanosTrabalho}" value="Total de Discentes com Planos de Trabalho" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalDiscentesParticipantesPlanoTrabalhoExtensao}" value="Total de Discentes com Planos de Trabalho Participantes de A��es de Extens�o" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscenteNaEquipe}" value="Total de Discentes como membros da equipe" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesProjetoDiscentes}" value="Total de Discentes das Equipes dos Projetos Participantes de A��es de Extens�o" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesDocentesExtensao}" value="Total de Docentes Participantes de A��es de Extens�o" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesPorNivel}" value="Total de Docentes por N�vel" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesDetalhado}" value="Total de Docentes por Tipo de A��o" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioExternoDetalhado}" value="Total de Participantes Externos por Tipo de A��o" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoProdutos}" value="Total de Produtos Ativos por �rea Tem�tica" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPublicoAtingido.iniciarRelatorioPublicoAtingido}" value="Total de P�blico Atingido com Base nos Relat�rios Submetidos" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de P�blico Estimado x P�blico Atingido" onclick="setAba('extensao')"/></li>
		<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioTecnicoAdmDetalhado}" value="Total de T�cnicos Admin. por Tipo de A��o" onclick="setAba('extensao')"/></li>
	</ul>
	</li>	
</ul>