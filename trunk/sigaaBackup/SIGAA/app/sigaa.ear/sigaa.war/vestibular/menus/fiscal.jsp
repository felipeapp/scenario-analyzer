<ul>
	<li>Consultas
	<ul>
		<li><h:commandLink action="#{fiscal.listar}" value="Consultar Fiscais" onclick="setAba('fiscais')" /></li>
	</ul>
	</li>
	<li>Opera��es com Fiscais
	<ul>
		<li><h:commandLink action="#{associacaoFiscalLocalAplicacao.iniciar}" value="Alocar Fiscais aos Locais de Aplica��o de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{associacaoFiscalLocalAplicacao.iniciarOutraCidade}" value="Alocar Fiscais com Disponibilidade de Viajar para Outras Cidades" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{justificativaAusencia.listar}" value="Analisar Justificativas de Aus�ncia" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{justificativaAusencia.listarFiscaisAusentes}" value="Cadastrar Justificativa Avulsa de Fiscal" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarConvocacaoReservas}" value="Convocar Fiscais Reservas" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarPresencaReuniao}" value="Marcar Presen�a de Fiscais em Reuni�o" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{conceitoFiscal.iniciarRegistroConceito}" value="Registrar Conceito" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{assiduidadeFiscal.iniciarFrequenciaAplicacao}" value="Registrar Frequ�ncia de Aplica��o" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{selecaoManualFiscal.iniciar}" value="Selecionar Fiscais Manualmente" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{transferenciaFiscal.iniciarTransferencia}" value="Transferir Fiscais entre Locais de Aplica��o de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{validacaoFotoFiscalBean.listar}" value="Valida��o das Fotos" onclick="setAba('fiscais')" />
	</ul>
	</li>
	<li>Processamento
	<ul>
		<li><h:commandLink action="#{processamentoSelecaoFiscal.iniciar}" value="Sele��o de Fiscais" onclick="setAba('fiscais')" /></li>
	</ul>
	</li>
	<li>Relat�rios
	<ul>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaLocaisProva}" value="Locais de Aplica��o de Prova" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaInscritos}" value="Inscritos por Tipo" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFiscaisSelecionados}" value="Fiscais Selecionados" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFrequenciaReuniao}" value="Lista de Freq��ncia (assinatura) da Reuni�o" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaDistribuicaoFiscais}" value="Lista de Distribui��o de Fiscais no Local de Aplica��o" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFrequenciaAplicacao}" value="Lista de Freq��ncia (assinatura) da Aplica��o" onclick="setAba('fiscais')" /></li>
		<li><h:commandLink action="#{relatoriosVestibular.iniciarFichaAvaliacaoFiscal}" value="Fichas de Avalia��o Individual" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaFiscaisNaoAlocados}" value="Lista de Fiscais N�o Alocados" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarListaContatoFiscaisReservas}" value="Lista de Contato de Fiscais" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarResumoSelecaoFiscais}" value="Resumo do Processamento da Sele��o de Fiscais" onclick="setAba('fiscais')" />
		<li><h:commandLink action="#{relatoriosVestibular.iniciarFrequenciaFiscais}" value="Frequ�ncia de Aplica��o para Confer�ncia" onclick="setAba('fiscais')" />
		</li>
	</ul>
	</li>
</ul>