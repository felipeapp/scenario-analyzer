<ul>
	<li>Consultas
	<ul>
		<li><h:commandLink action="#{fiscal.listar}" value="Consultar Fiscais" onclick="setAba('fiscais')" /></li>
	</ul>
	</li>
	<li>Operações com Fiscais
	<ul>
		<li><h:commandLink action="#{associacaoFiscalLocalAplicacao.iniciar}" value="Alocar Fiscais aos Locais de Aplicação de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{associacaoFiscalLocalAplicacao.iniciarOutraCidade}" value="Alocar Fiscais com Disponibilidade de Viajar para Outras Cidades" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{justificativaAusencia.listar}" value="Analisar Justificativas de Ausência" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{justificativaAusencia.listarFiscaisAusentes}" value="Cadastrar Justificativa Avulsa de Fiscal" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarConvocacaoReservas}" value="Convocar Fiscais Reservas" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarPresencaReuniao}" value="Marcar Presença de Fiscais em Reunião" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{conceitoFiscal.iniciarRegistroConceito}" value="Registrar Conceito" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarFrequenciaAplicacao}" value="Registrar Frequência de Aplicação" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{selecaoManualFiscal.iniciar}" value="Selecionar Fiscais Manualmente" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{transferenciaFiscal.iniciarTransferencia}" value="Transferir Fiscais entre Locais de Aplicação de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{validacaoFotoFiscalBean.listar}" value="Validação das Fotos" onclick="setAba('fiscais')" />
	</ul>
	</li>
	<li>Processamento
	<ul>
		<li><h:commandLink action="#{processamentoSelecaoFiscal.iniciar}" value="Seleção de Fiscais" onclick="setAba('fiscais')" /></li>
	</ul>
	</li>
	<li>Relatórios
	<ul>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaLocaisProva}" value="Locais de Aplicação de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaInscritos}" value="Inscritos por Tipo" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFiscaisSelecionados}" value="Fiscais Selecionados" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFrequenciaReuniao}" value="Lista de Freqüência (assinatura) da Reunião" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaDistribuicaoFiscais}" value="Lista de Distribuição de Fiscais no Local de Aplicação" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFrequenciaAplicacao}" value="Lista de Freqüência (assinatura) da Aplicação" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarFichaAvaliacaoFiscal}" value="Fichas de Avaliação Individual" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFiscaisNaoAlocados}" value="Lista de Fiscais Não Alocados" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaContatoFiscaisReservas}" value="Lista de Contato de Fiscais" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarResumoSelecaoFiscais}" value="Resumo do Processamento da Seleção de Fiscais" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarFrequenciaFiscais}" value="Frequência de Aplicação para Conferência" onclick="setAba('fiscais')" />
		</li>
	</ul>
	</li>
</ul>