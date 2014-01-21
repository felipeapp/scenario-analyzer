<ul>
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO } %>">
	 	<li>Processo Seletivo
	 		<ul>
				<li><h:commandLink action="#{processoSeletivoTecnico.preCadastrar}" value="Cadastrar" onclick="setAba('importacao')" id="processoSeletivoTecnico_preCadastrar"/></li>
				<li><h:commandLink action="#{processoSeletivoTecnico.listar}" value="Listar/Alterar" onclick="setAba('importacao')" id="processoSeletivoTecnico_listar"/></li>
	 		</ul>
	 	</li>
	 	
	 	<li>Importa��o
			<ul>
				<li><h:commandLink action="#{importaAprovadosTecnicoMBean.iniciarDefinicaoLeiaute}" value="Definir Leiaute do Arquivo de Importa��o" onclick="setAba('importacao')" id="importaInscricaoTecnicoMBean_iniciarDefinicaoLeiaute"/></li>
				<li><h:commandLink action="#{importaAprovadosTecnicoMBean.listarLeiautes}" value="Listar Leiautes do Arquivo de Importa��o" onclick="setAba('importacao')" id="importaInscricaoTecnicoMBean_listarLeiautes"/></li>
				<li><h:commandLink action="#{importaAprovadosTecnicoMBean.iniciarImportacao}" value="Importar Aprovados" onclick="setAba('importacao')" id="importaAprovadosTecnicoMBean_iniciarImportacao"/></li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO } %>"> 	
		<li>Cadastramento
	 		<ul>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.iniciarPreCadastro}" value="Realizar Pr�-cadastramento de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_iniciar"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.iniciar}" value="Confirmar Cadastramento de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_iniciarPreCadastramento"/></li>
	 		</ul>
	 	</li>
	 	
	 		<li>Relat�rios
	 		<ul>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioGeralDeClassificacao}" value="Relat�rio Geral de Classifica��o" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioGeralDeClassificacao"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioDeConvocacoes}" value="Relat�rio de Convoca��es de Discentes" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioDeConvocacoes"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioDeCadastramento}" value="Relat�rio de Cadastramento" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioDeCadastramento"/></li>
				<li><h:commandLink action="#{cadastramentoDiscenteTecnico.consultarIndeferimentos}" value="Consultar Indeferimentos" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_consultarIndeferimentos"/></li>
				<%--<li><h:commandLink action="#{cadastramentoDiscenteTecnico.relatorioQuantitativoConvocadosCadastrados}" value="Relat�rio Quantitativo de discentes Convocados X Cadastrados" onclick="setAba('importacao')" id="cadastramentoDiscenteTecnico_relatorioQuantitativo"/></li>--%>
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
			<li> <h:commandLink id="emitirAtestadoMatriculaTecnicoIMD" action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('importacao')"/> </li>
		</ul>
	</li>

</ul>