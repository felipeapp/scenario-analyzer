<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD }%>">
		<li><h:commandLink action="#{convocacaoProcessoSeletivoTecnicoIMD.iniciarConvocacaoImportacao}" value="Convocar Candidatos" onclick="setAba('importacao')" id="concocacaoProcessoSeletivoTecnicoIMD_iniciar"/></li>
		<li><h:commandLink action="#{cadastramentoDiscenteTecnico.iniciarEnvioEmail}" value="Enviar E-mail aos Candidatos" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_iniciarEnvioEmail"/></li>	
		
 	</ufrn:checkRole>
	
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD } %>"> 	
		<li>Cadastramento
	 		<ul>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.iniciarPreCadastro}" value="Realizar Pré-cadastramento de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_iniciar"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.iniciar}" value="Confirmar Cadastramento de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_iniciarPreCadastramento"/></li>
	 		</ul>
	 	</li>
	 	
	 		<li>Relatórios
	 		<ul>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioGeralDeClassificacao}" value="Relatório Geral de Classificação" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioGeralDeClassificacao"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioDeConvocacoes}" value="Relatório de Convocações de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioDeConvocacoes"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioDeCadastramento}" value="Relatório de Cadastramento" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioDeCadastramento"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.consultarIndeferimentos}" value="Consultar Indeferimentos" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_consultarIndeferimentos"/></li>
				
	 		</ul>
	 	</li>
	</ufrn:checkRole>
	
	<li>Consultas
		<ul>
			<li> <h:commandLink id="consultarDadosDiscentesIMD" action="#{ consultaDadosDiscentesIMD.iniciarBusca }" value="Consultar Dados dos Discentes" onclick="setAba('importacao')"/> </li>
		</ul>
	</li>
	
	<li>Documentos
		<ul>
			<li> <h:commandLink id="emitirAtestadoMatriculaTecnicoIMD" action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('importacao')"/> </li>
		</ul>
	</li>

</ul>