<ul>
	<li>Valida��o de Candidatos
		<ul>
			<li><h:commandLink action="#{validacaoFotoBean.iniciarAtualizacaoFotoLote}" value="Altera��o das Fotos em Lote" onclick="setAba('candidatos')" id="alteracao_foto_em_lote"/></li>
			<li><h:commandLink action="#{validacaoCandidatoBean.iniciarValidacaoLote}" value="Validar Inscri��es em Lote" onclick="setAba('candidatos')" id="validacaoCandidatoBean_iniciarValidacaoLote"/></li>
			<li><h:commandLink action="#{validacaoCandidatoBean.listar}" value="Validar/Invalidar Inscri��es" onclick="setAba('candidatos')" id="validacaoCandidatoBean_listar"/></li>
			<li>
				<h:commandLink action="#{validacaoFotoBean.listar}" value="Validar as Fotos 3x4 dos Candidatos" onclick="setAba('candidatos')" id="validacaoFotoBean_listar"/>
				<c:set var="qtdPendente" value="#{validacaoFotoBean.qtdPendenteValidacao}" />
				<h:outputText value=" (#{qtdPendente} pendentes de valida��o)" rendered="#{qtdPendente > 0}" style="color: red;"/>
			</li>
			<li><h:commandLink action="#{importaLocalProvaCandidatoBean.iniciarImportacaoDados}" value="Definir Local de Prova em Lote" onclick="setAba('candidatos')" id="importaLocalProvaCandidatoBean_iniciarImportacaoDados"/></li>
			<li><h:commandLink action="#{processaPagamentoGRUMBean.iniciar}" value="Processar Pagamentos de GRUs" onclick="setAba('candidatos')" id="processaPagamentoGRUMBean_iniciar"/></li>
		</ul>
	</li>

	<li>Consultas e Relat�rios
		<ul>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarDemandaPorCurso}" value="Demanda de Candidatos Inscritos por Curso" onclick="setAba('candidatos')" id="relatoriosVestibular_iniciarDemandaCurso"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarEstatisticaInscritosPorSemana}" value="Estat�stica de Inscritos por Semana" onclick="setAba('candidatos')" id="relatoriosVestibular_iniciarEstatisticaInscritosPorSemana"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarEstatisticaBeneficiariosInsencaoInscricao}" value="Estat�sticas dos Beneficiados com a Isen��o na Inscri��o" onclick="setAba('candidatos')" id="relatoriosVestibular_iniciarBeneficiariosIsencaoInscricao"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarEstatisticaInscritosDiario}" value="Estat�sticas dos Candidatos Inscritos por Dia" onclick="setAba('candidatos')" id="relatoriosVestibular_iniciarEstatisticaInscritosPorDiarias"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarEstatisticaPaganteIsentoInscricao}" value="Estat�sticas dos Pagantes X Isentos Inscritos no Vestibular" onclick="setAba('candidatos')" id="relatoriosVestibular_iniciarEstatisticaPagantesIsentos"/></li>
			<li><h:commandLink action="#{exportarDadosCandidatoBean.iniciarDadosInscritos}" value="Exportar Dados dos Candidatos" onclick="setAba('candidatos')" id="exportarDadosCandidatoBean_iniciarDadosCandidato"/></li>
			<li><h:commandLink action="#{exportarDadosCandidatoBean.iniciarFotosCandidato}" value="Exportar Fotos dos Candidatos" onclick="setAba('candidatos')" id="exportarDadosCandidatoBean_iniciarFotosCandidato"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarQuantitativoStatusFotos}" value="Quantitativo de Situa��es de Fotos" onclick="setAba('candidatos')" id="relatoriosVestibular_quantitativoSituacoesFoto"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarRelatorioSocioEconomico}" value="Relat�rio Estat�stico das Respostas ao Question�rio Socioecon�mico" onclick="setAba('candidatos')" id="relatorio_socio_economico"/></li>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarRelatorioGRUPagas}" value="Relat�rio de GRUs Pagas" onclick="setAba('candidatos')" id="iniciarRelatorioGRUPagas"/></li>
		</ul>
	</li>
	
	<li>Dados Pessoais
		<ul>
			<li><h:commandLink action="#{relatoriosVestibular.iniciarVisualizacaoDadosCandidato}" value="Consultar/Alterar Dados do Candidato" onclick="setAba('candidatos')" id="Consultar_candidato"/></li>
			<li><h:commandLink action="#{correcaoEmailCandidatoMBean.iniciar}" value="Corre��o de E-Mail de Candidato" onclick="setAba('candidatos')" id="dadosPessoais_iniciarCorrecaoEmail"/></li>
		</ul>
	</li>
</ul>