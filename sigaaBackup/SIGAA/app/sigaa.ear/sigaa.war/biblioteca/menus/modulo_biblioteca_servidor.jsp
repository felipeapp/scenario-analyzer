<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f"    uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<div class="descricaoOperacao">
	Nesta aba você pode configurar seu usuário da biblioteca, assim como verificar relatório de histórico de empréstimos e renovar
	seus empréstimos ativos, entre outros serviços da biblioteca.
</div>

<ul>

	<li>
		<h:commandLink id="cmdCadastrarNaBiblioteca" value="Cadastrar para Utilizar os Serviços da Biblioteca" action="#{cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro}" onclick="setAba('modulo_servidor')" />
	</li>

	<br/>
	<br/>

	<li>
		<h:commandLink id="cmdBuscaPublicaAcervo" action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" value="Pesquisar Material no Acervo" onclick="setAba('modulo_servidor')" />
	</li>
	
	<li>
		<h:commandLink id="cmdPesqArtigoAcervo" action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" value="Pesquisar Artigo no Acervo" onclick="setAba('modulo_servidor')" />
	</li>
	
	<br/>
	<br/>
	
	
	<li>Empréstimos
		<ul>
		
			<li>
				<h:commandLink id="cmdVisualizaMeusEmprestimosAtivos" value="Visualizar Empréstimos Ativos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosAtivos}" onclick="setAba('modulo_servidor')" />
			</li>
		
			<li>
				<h:commandLink id="cmdRenovarMeusEmprestimos" value="Renovar Meus Empréstimos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" onclick="setAba('modulo_servidor')" />
			</li>
			
			<li>
				<h:commandLink id="cmdMeusHistoricoEmprestimos" value="Meu Histórico de Empréstimos" action="#{emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado}" onclick="setAba('modulo_servidor')" />
			</li>
			
			<c:if test="${emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}">
				<li>
					<h:commandLink id="cmdImprimirGRUs" value="Imprimir GRU para pagamentos de multas" action="#{emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas}" onclick="setAba('modulo_servidor')" />
				</li>
			</c:if>
			
		</ul>
	</li>
	
	
	<li> Disseminação Seletiva da Informação 
		<ul>
			<li><h:commandLink action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" value="Cadastrar Interesse" onclick="setAba('modulo_servidor')" /> </li>
		</ul>
	</li>
	
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	
	<li>
		<h:commandLink id="cmdVerificarMinhaSituacao" value="Verificar minha Situação / Emitir Documento de Quitação " action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado}" onclick="setAba('modulo_servidor')" />
	</li>
	
	
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
	<br/>
	<br/>
	<br/>
	<br/>
	
	<c:if test="${solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}"> 
		<li>Reservas de Materiais Bibliográficos
			<ul>
				<li>
					<h:commandLink id="cmdVisualizarReserva" value="Visualizar Minhas Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas}" onclick="setAba('modulo_servidor')" />
				</li>
			</ul>
			<ul>
				<li>
					<h:commandLink id="cmdRealizarReserva" value="Solicitar Nova Reserva" action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario}" onclick="setAba('modulo_servidor')" />
				</li>
			</ul>
		</li>
	</c:if>
	
	
	<li> Serviços ao Usuário
		<ul>
			<c:if test="${solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}">
				<li>	
					<h:commandLink id="cmdSolicitarOrientacao" value="Agendamento de Orientação" 
						action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}"
						onclick="setAba('modulo_servidor')" />
				</li>
			</c:if>
			
			<c:if test="${solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}">
				<li>	
					<h:commandLink id="cmdSolicitarCatalogacao" value="Catalogação na Fonte" 
						action="#{solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes}" 
						onclick="setAba('modulo_servidor')" />
				</li>
			</c:if>
			
			<c:if test="${solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}">
				<li>	
					<h:commandLink id="cmdSolicitarNormalizacao" value="Normalização" 
						action="#{solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes}" 
						onclick="setAba('modulo_servidor')" />
				</li>
			</c:if>
		</ul>
	</li>
		
	
	
	
	<%-- Caso de uso Suspenso 
	<ufrn:checkRole papeis="${ levantamentoInfraMBean.papeisUsuario }">
		<li>Solicitações
			<ul>
				<li>
	    		<h:commandLink value="Levantamento de Infra-Estrutura" action="#{ levantamentoInfraMBean.listarParaUsuario }" id="levantamentoInfra" />
	       		</li>
	       	</ul>
		</li>
   </ufrn:checkRole>
	--%>
	
	
	
	<%-- Funcoes exclusiva para DOCENTES   --%>
	
	<c:if test="${sessionScope.usuario.servidor != null && sessionScope.usuario.servidor.docente}">
		<li>Compras de Livros
			<ul>
				<li>
					<h:commandLink id="bib_Sol_Compra_Livro" actionListener="#{menuDocente.redirecionar}" value="Solicitar Compra de Livros" onclick="setAba('modulo_servidor')">
						<f:param name="url" value="/entrarSistema.do?sistema=sipac&url=/sipac/manipulaReqBiblioteca.do?acao=474" />
					</h:commandLink>
				</li>
				<li>
					<h:commandLink id="bib_Acom_Compra_Livro" actionListener="#{menuDocente.redirecionar}" value="Acompanhar Solicitações de Compra de Livros" onclick="setAba('modulo_servidor')">
						<f:param name="url" value="/entrarSistema.do?sistema=sipac&url=/sipac/manipulaReqBiblioteca.do?acao=480" />
					</h:commandLink>
				</li>
				<li>
					<h:commandLink id="cmdRelatorioCompras" actionListener="#{menuDocente.redirecionar}" value="Relatório de Novas Compras" onclick="setAba('modulo_servidor')">
						<f:param name="url" value="/public/biblioteca/buscaPublicaAquisicoes.jsf?aba=p-biblioteca" />
					</h:commandLink>
				</li>
				<li>
					<h:commandLink id="cmdRelatorioAquisicoes" actionListener="#{menuDocente.redirecionar}" value="Relatório de Novas Aquisições" onclick="setAba('modulo_servidor')">
						<f:param name="url" value="/public/biblioteca/buscaPublicaNovasAquisicoes.jsf?aba=p-biblioteca" />
					</h:commandLink>
				</li>
			</ul>
		</li>
	</c:if>
	
	
	
</ul>