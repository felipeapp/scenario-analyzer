<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<div class="descricaoOperacao">
	Controla todas as atividades relativas ao empréstimo de exemplares, tais como:
	organização das estantes, cobrança de multas e sanções, separação de material para pequenos reparos, 
	fornecimento de declaração de quitação, controle estatístico de crescimento da coleção, 
	controle estatístico dos materiais consultados.
</div>

<ul>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Módulo de Circulação
			<ul>
				<li>
					<h:commandLink value="Realizar Empréstimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')">
							<f:param name="operacao" value="1" />	
					</h:commandLink>
				</li>
				<li>
					<h:commandLink value="Renovar Empréstimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')" >
						<f:param name="operacao" value="2" />
					</h:commandLink>
				</li>
				<li><h:commandLink value="Devolver Empréstimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')" >
						<f:param name="operacao" value="3" />
					</h:commandLink>
				</li>
			</ul>
		</li>
		</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Gerenciamentos dos Usuários da Biblioteca
			<ul>	
				
				<li>
				   <h:commandLink value="Visualizar os Vínculos dos Usuários no Sistema" action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacao}"  onclick="setAba('circulacao')" />			   
				</li>
			
				<li>
				 	<h:commandLink value="Cadastrar / Alterar Senha" action="#{cadastroUsuarioBibliotecaMBean.iniciarCadastroUsuarioPeloOperador}"  onclick="setAba('circulacao')" />
				</li>
				
				<li>
					<h:commandLink value="Bloquear/Desbloquear Usuários" action="#{bloquearUsuarioBibliotecaMBean.iniciarBloqueio}"  onclick="setAba('circulacao')" />
				</li>
				
				<li>
					<h:commandLink value="Emitir Termo de Adesão" action="#{emiteTermoAdesaoBibliotecaMBean.iniciar}"	onclick="setAba('circulacao')" />
				</li>
				
				<li> 
					<h:commandLink value="Listar os Empréstimos Ativos de um Usuário" action="#{listaEmprestimosAtivosUsuarioMBean.iniciaListaEmprestimos}"  onclick="setAba('circulacao')"/>
				</li>
				
				<li> 
					<h:commandLink value="Histórico de Empréstimos de um Usuário" action="#{emiteHistoricoEmprestimosMBean.iniciaEscolhendoUsuario}"  onclick="setAba('circulacao')" />
				</li>
				
				 <li>
					<h:commandLink value="Histórico de Empréstimos de um Material" action="#{emiteRelatorioHistoricosMBean.iniciarConsultaEmprestimos}"  onclick="setAba('circulacao')" />
				</li>
				
				
				<li>Mensagens aos Usuários
					<ul>
						<li> <h:commandLink value="Enviar Mensagens Individuais" action="#{enviaMensagemUsuariosBibliotecaMBean.iniciarEnvioIndividual}"  onclick="setAba('circulacao')" />
						</li>
					</ul>
				</li>
				 
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,  SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO}  %>">
		<li>Controle de Empréstimos
			<ul>
				
				<li>
				   <h:commandLink value="Verificar Situação do Usuário / Emitir Declaração de Quitação" action="#{verificaSituacaoUsuarioBibliotecaMBean.iniciarVerificacao}"  onclick="setAba('circulacao')" />			   
				</li>
				
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Desfazer a Quitação de um Vínculo" action="#{desfazQuitacaoVinculoUsuarioBibliotecaMBean.iniciar}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Empréstimo" action="#{listaEmprestimosAtivosUsuarioMBean.iniciaListaEmprestimos}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Renovação" action="#{listaRenovacoesAtivasUsuarioMBean.iniciaListaRenovacoes}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Devolução" action="#{listaDevolucoesRecentesUsuarioMBean.iniciaListaDevolucoes}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
			</ul>
		</li>
	</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Usuários Externos
			<ul>
				<li>
					<h:commandLink value="Cadastrar / Alterar Vínculo" action="#{usuarioExternoBibliotecaMBean.iniciarCadastroUsuarioExterno}"  onclick="setAba('circulacao')" />
				</li>
				<li>
					<h:commandLink value="Cancelar Vínculo" action="#{usuarioExternoBibliotecaMBean.iniciarCancelamentoVinculo}"  onclick="setAba('circulacao')"/>
				</li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	
	<%-- Por enquanto Só quem está fazendo empréstimos institucionais é o setor de informação e referencam, por isso o link comentado 
	<li>Bibliotecas/Unidades Externas
		<ul>
			<li><h:commandLink action="#{bibliotecaExternaMBean.listar}" onclick="setAba('circulacao')" value="Listar / Cadastrar Nova Biblioteca ou Unidade Externa" /></li>
		</ul>
	</li> --%>
	
	
	



	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		
		<li> Notas de Circulação
			<ul>
			
			<li> <h:commandLink value="Incluir Nota de Circulação" action="#{notasCirculacaoMBean.listar}"  onclick="setAba('circulacao')" /> </li>
			
			<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialIncluirNota}" value="Incluir Nota de Circulação em Vários Materiais"
							onclick="setAba('circulacao')" id="cmdIncluiNotaCirculacaoVariosMateriaisCirculacao" /> 
			</li>
			
			<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialRemoverNota}" value="Remover Nota de Circulação em Vários Materiais"
					onclick="setAba('circulacao')" id="cmdRemoverNotaCirculacaoVariosMateriaisCirculacao" /> 
			</li>
			
			</ul>
		</li>
		
	</ufrn:checkRole>




	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Interrupções das Bibliotecas
			<ul>
				<li><h:commandLink action="#{interrupcaoBibliotecaMBean.iniciarCasdastroInterrupcao}" onclick="setAba('circulacao')" value="Listar / Cadastrar Nova Interrupção" /></li>
				<li><h:commandLink action="#{interrupcaoBibliotecaMBean.iniciarVisualizarHistoricoInterrupcao}" onclick="setAba('circulacao')" value="Visualizar Histórico de Interrupções" /></li>
			</ul>
		</li>
	</ufrn:checkRole>
	

	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Registrar Estatísticas Movimento Usuário
			<ul>
				<li>
					<h:commandLink value="Cadastrar Movimentação Diária de Usuários" action="#{registroFrequenciaUsuariosBibliotecaMBean.iniciarRegistroFrequenciaUsuario}"  onclick="setAba('circulacao')"/>
				</li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	<%-- Por enquanto Só quem está fazendo empréstimos institucionais é o setor de informação e referencam, por isso o link comentado 
	<li>Empréstimos Institucionais
		<ul>
			<li>Bibliotecas/Unidades Internas
				<ul>
					<li>
						<h:commandLink value="Listar/Realizar Novo Empréstimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('circulacao')">
							<f:param name="emprestimoParaBibliotecaExterna" value="false" />
							<f:param name="limpaLista" value="true" />
						</h:commandLink>
					</li>
				</ul>
			</li>
			<li>Bibliotecas/Unidades Externas
				<ul>
					<li>
						<h:commandLink value="Listar/Realizar Novo Empréstimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('circulacao')">
							<f:param name="emprestimoParaBibliotecaExterna" value="true" />
							<f:param name="limpaLista" value="true" />
						</h:commandLink>
					</li>
				</ul>
			</li>
		</ul>
	</li> --%>
	
	<li> Gerenciamento de Punições
		<ul>
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<li>
					<h:commandLink value="Gerenciar Suspensões" action="#{suspensaoUsuarioBibliotecaMBean.iniciarGerenciaSuspensoes}" onclick="setAba('circulacao')" />
				</li>
			</ufrn:checkRole>
			
			<%-- Pagamento de multas qualquer usuário de circulação vai poder fazer --%>
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<li>
					<h:commandLink value="Gerenciar Multas" action="#{multasUsuarioBibliotecaMBean.iniciarGerenciarMultas}" onclick="setAba('circulacao')" />
				</li>
			</ufrn:checkRole>
		</ul>
	</li>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li> Gerenciamento de Reservas
			<ul>
				<li>
					<h:commandLink value="Visualizar/Solicitar Reservas para um Usuário" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoReservasDeUmUsuario}" onclick="setAba('circulacao')" />
				</li>
				<li>
					<h:commandLink value="Visualizar Reservas de um Título" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoReservasDeUmTitulo}" onclick="setAba('circulacao')" />
				</li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Materiais Perdidos
			<ul>
				<li>
					<h:commandLink value="Comunicar Material Perdido" action="#{comunicarMaterialPerdidoMBean.iniciarComunicacao}" onclick="setAba('circulacao')"/>
				</li>
				<li>
					<h:commandLink value="Listar Comunicações de Materiais Perdidos" action="#{comunicarMaterialPerdidoMBean.listarUsuariosComMateriaisPerdidos}" onclick="setAba('circulacao')" />
				</li>
			</ul>
			
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li> <h:commandLink action="#{transferirMateriaisEntreSetoresBibliotecaMBean.iniciarBuscaMaterial}" value="Transferir Materiais Entre Setores"
				onclick="setAba('circulacao')" id="cmdTransferirMateriaisEntreSetoresCirculacao" /> 
		</li>
	</ufrn:checkRole>
	
	<br/><br/><br/>
	
</ul>