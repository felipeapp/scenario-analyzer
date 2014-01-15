    
    <ul>
    	<li><h:commandLink action="#{ cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro }" value="Cadastre-se para Utilizar os Serviços da Biblioteca"  /> </li>
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
		<li> Empréstimos
			<ul>
				<li><h:commandLink action="#{ meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis }" value="Renovar Meus Empréstimos"  /> </li>
				<li><h:commandLink action="#{ emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado }" value="Meu Histórico de Empréstimos"  /> </li>
				<li><h:commandLink action="#{ emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas }" value="Imprimir GRU para pagamentos de multas" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" /> </li>
			</ul>
		</li>
	</ul>

	<ul>
		<li> Disseminação Seletiva da Informação 
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
		<li><h:commandLink action="#{ verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado }" value="Verificar minha Situação / Emitir Documento de Quitação  "  /> </li>
	</ul>
	
	<li>Informações ao Usuário
		<ul>
			<li>
				<h:commandLink id="cmdVisulizarMeusVinculos" value="Visualizar meus Vínculos no Sistema " action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" onclick="setAba('modulo_servidor')" />
			</li>
		</ul>
		<ul>
			<li>
				<h:commandLink id="cmdVisualizarPoliticasEmprestimo" value="Visualizar as Políticas de Empréstimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" onclick="setAba('modulo_servidor')" />
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
	
	
	<%--  Ainda não está em produção --%>
	<ul>		
		<li> Serviços ao Usuário
			<ul>
				<li><h:commandLink action="#{ solicitacaoOrientacaoMBean.verMinhasSolicitacoes }" value="Agendamento de Orientação" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" /> </li>
				<li><h:commandLink action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" value="Normalização" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" /> </li>
				<li><h:commandLink action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" value="Catalogação na Fonte" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" /> </li>
			</ul>
		</li>
	</ul>
	 
	
     
    <br/>
    <br/>
    
	<ul>		
		<li> Compra de Livros
			<ul>
				<li><a href="${ctx}/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474">Solicitar Compra de Livros para a Biblioteca</a></li>
				<li><a href="${ctx}/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480">Acompanhar Solicitação de Livros</a></li>
		    </ul>
    	</li>
	</ul>
    
    <br/>
    <br/>
    <br/>
    <br/>
    