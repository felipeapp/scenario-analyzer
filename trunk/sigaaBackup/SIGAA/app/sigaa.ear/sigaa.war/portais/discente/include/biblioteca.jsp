    
    <ul>
    	<li><h:commandLink action="#{ cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro }" value="Cadastre-se para Utilizar os Servi�os da Biblioteca"  /> </li>
    </ul>
    
    <ul>
		<li> Pesquisas
		    <ul>
				<li><h:commandLink action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" value="Pesquisar Material no Acervo"  /> </li>
				<li><h:commandLink action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" value="Pesquisar Artigo no Acervo"  /> </li>
			</ul>
		</li>
	</ul>
	
	<br/>
	<br/>
	
	<ul>
		<li> Empr�stimos
			<ul>
				<li><h:commandLink action="#{ meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis }" value="Renovar Meus Empr�stimos"  /> </li>
				<li><h:commandLink action="#{ emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado }" value="Meu Hist�rico de Empr�stimos"  /> </li>
				<li><h:commandLink action="#{ emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas }" value="Imprimir GRU para pagamentos de multas" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" /> </li>
			</ul>
		</li>
	</ul>

	<ul>
		<li> Dissemina��o Seletiva da Informa��o 
		    <ul>
				<li><h:commandLink action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" value="Cadastrar Interesse" /> </li>
			</ul>
		</li>
	</ul>

	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>

	<ul>
		<li><h:commandLink action="#{ verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado }" value="Verificar minha Situa��o / Emitir Documento de Quita��o  "  /> </li>
	</ul>
	
	<li>Informa��es ao Usu�rio
		<ul>
			<li>
				<h:commandLink id="cmdVisulizarMeusVinculos" value="Visualizar meus V�nculos no Sistema " action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" onclick="setAba('modulo_servidor')" />
			</li>
		</ul>
		<ul>
			<li>
				<h:commandLink id="cmdVisualizarPoliticasEmprestimo" value="Visualizar as Pol�ticas de Empr�stimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" onclick="setAba('modulo_servidor')" />
			</li>
		</ul>
	</li>
	
	<br/>
	<br/>
	
	<c:if test="${ solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}">
		<ul>		
			<li> Reservas de Materiais
				<ul>
					<li><h:commandLink action="#{ visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas }" value="Visualizar Minhas Reservas"  /> </li>
					<li><h:commandLink action="#{ solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario }" value="Solicitar Nova Reserva"  /> </li>
				</ul>
			</li>
		</ul>
	</c:if>
	
	
	<%--  Ainda n�o est� em produ��o --%>
	<ul>		
		<li> Servi�os ao Usu�rio
			<ul>
				<li><h:commandLink action="#{ solicitacaoOrientacaoMBean.verMinhasSolicitacoes }" value="Agendamento de Orienta��o" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" /> </li>
				<li><h:commandLink action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" value="Normaliza��o" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" /> </li>
				<li><h:commandLink action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" value="Cataloga��o na Fonte" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" /> </li>
			</ul>
		</li>
	</ul>
	 
	
     
    <br/>
    <br/>
    
	<ul>		
		<li> Compra de Livros
			<ul>
				<li><a href="${ctx}/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474">Solicitar Compra de Livros para a Biblioteca</a></li>
				<li><a href="${ctx}/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480">Acompanhar Solicita��o de Livros</a></li>
		    </ul>
    	</li>
	</ul>
    
    <br/>
    <br/>
    <br/>
    <br/>
    