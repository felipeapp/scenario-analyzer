<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<div class="descricaoOperacao">
	Controla todas as atividades relativas ao empr�stimo de exemplares, tais como:
	organiza��o das estantes, cobran�a de multas e san��es, separa��o de material para pequenos reparos, 
	fornecimento de declara��o de quita��o, controle estat�stico de crescimento da cole��o, 
	controle estat�stico dos materiais consultados.
</div>

<ul>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>M�dulo de Circula��o
			<ul>
				<li>
					<h:commandLink value="Realizar Empr�stimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')">
							<f:param name="operacao" value="1" />	
					</h:commandLink>
				</li>
				<li>
					<h:commandLink value="Renovar Empr�stimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')" >
						<f:param name="operacao" value="2" />
					</h:commandLink>
				</li>
				<li><h:commandLink value="Devolver Empr�stimo" action="#{moduloCirculacaoMBean.iniciar}"  onclick="setAba('circulacao')" >
						<f:param name="operacao" value="3" />
					</h:commandLink>
				</li>
			</ul>
		</li>
		</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Gerenciamentos dos Usu�rios da Biblioteca
			<ul>	
				
				<li>
				   <h:commandLink value="Visualizar os V�nculos dos Usu�rios no Sistema" action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacao}"  onclick="setAba('circulacao')" />			   
				</li>
			
				<li>
				 	<h:commandLink value="Cadastrar / Alterar Senha" action="#{cadastroUsuarioBibliotecaMBean.iniciarCadastroUsuarioPeloOperador}"  onclick="setAba('circulacao')" />
				</li>
				
				<li>
					<h:commandLink value="Bloquear/Desbloquear Usu�rios" action="#{bloquearUsuarioBibliotecaMBean.iniciarBloqueio}"  onclick="setAba('circulacao')" />
				</li>
				
				<li>
					<h:commandLink value="Emitir Termo de Ades�o" action="#{emiteTermoAdesaoBibliotecaMBean.iniciar}"	onclick="setAba('circulacao')" />
				</li>
				
				<li> 
					<h:commandLink value="Listar os Empr�stimos Ativos de um Usu�rio" action="#{listaEmprestimosAtivosUsuarioMBean.iniciaListaEmprestimos}"  onclick="setAba('circulacao')"/>
				</li>
				
				<li> 
					<h:commandLink value="Hist�rico de Empr�stimos de um Usu�rio" action="#{emiteHistoricoEmprestimosMBean.iniciaEscolhendoUsuario}"  onclick="setAba('circulacao')" />
				</li>
				
				 <li>
					<h:commandLink value="Hist�rico de Empr�stimos de um Material" action="#{emiteRelatorioHistoricosMBean.iniciarConsultaEmprestimos}"  onclick="setAba('circulacao')" />
				</li>
				
				
				<li>Mensagens aos Usu�rios
					<ul>
						<li> <h:commandLink value="Enviar Mensagens Individuais" action="#{enviaMensagemUsuariosBibliotecaMBean.iniciarEnvioIndividual}"  onclick="setAba('circulacao')" />
						</li>
					</ul>
				</li>
				 
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,  SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO}  %>">
		<li>Controle de Empr�stimos
			<ul>
				
				<li>
				   <h:commandLink value="Verificar Situa��o do Usu�rio / Emitir Declara��o de Quita��o" action="#{verificaSituacaoUsuarioBibliotecaMBean.iniciarVerificacao}"  onclick="setAba('circulacao')" />			   
				</li>
				
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Desfazer a Quita��o de um V�nculo" action="#{desfazQuitacaoVinculoUsuarioBibliotecaMBean.iniciar}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Empr�stimo" action="#{listaEmprestimosAtivosUsuarioMBean.iniciaListaEmprestimos}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Renova��o" action="#{listaRenovacoesAtivasUsuarioMBean.iniciaListaRenovacoes}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Estornar Devolu��o" action="#{listaDevolucoesRecentesUsuarioMBean.iniciaListaDevolucoes}"  onclick="setAba('circulacao')" />  
					</li>
				</ufrn:checkRole>
				
			</ul>
		</li>
	</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Usu�rios Externos
			<ul>
				<li>
					<h:commandLink value="Cadastrar / Alterar V�nculo" action="#{usuarioExternoBibliotecaMBean.iniciarCadastroUsuarioExterno}"  onclick="setAba('circulacao')" />
				</li>
				<li>
					<h:commandLink value="Cancelar V�nculo" action="#{usuarioExternoBibliotecaMBean.iniciarCancelamentoVinculo}"  onclick="setAba('circulacao')"/>
				</li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	
	
	<%-- Por enquanto S� quem est� fazendo empr�stimos institucionais � o setor de informa��o e referencam, por isso o link comentado 
	<li>Bibliotecas/Unidades Externas
		<ul>
			<li><h:commandLink action="#{bibliotecaExternaMBean.listar}" onclick="setAba('circulacao')" value="Listar / Cadastrar Nova Biblioteca ou Unidade Externa" /></li>
		</ul>
	</li> --%>
	
	
	



	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		
		<li> Notas de Circula��o
			<ul>
			
			<li> <h:commandLink value="Incluir Nota de Circula��o" action="#{notasCirculacaoMBean.listar}"  onclick="setAba('circulacao')" /> </li>
			
			<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialIncluirNota}" value="Incluir Nota de Circula��o em V�rios Materiais"
							onclick="setAba('circulacao')" id="cmdIncluiNotaCirculacaoVariosMateriaisCirculacao" /> 
			</li>
			
			<li> <h:commandLink action="#{modificarNotaCirculacaoVariosMateriaisMBean.iniciarBuscaMaterialRemoverNota}" value="Remover Nota de Circula��o em V�rios Materiais"
					onclick="setAba('circulacao')" id="cmdRemoverNotaCirculacaoVariosMateriaisCirculacao" /> 
			</li>
			
			</ul>
		</li>
		
	</ufrn:checkRole>




	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Interrup��es das Bibliotecas
			<ul>
				<li><h:commandLink action="#{interrupcaoBibliotecaMBean.iniciarCasdastroInterrupcao}" onclick="setAba('circulacao')" value="Listar / Cadastrar Nova Interrup��o" /></li>
				<li><h:commandLink action="#{interrupcaoBibliotecaMBean.iniciarVisualizarHistoricoInterrupcao}" onclick="setAba('circulacao')" value="Visualizar Hist�rico de Interrup��es" /></li>
			</ul>
		</li>
	</ufrn:checkRole>
	

	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Registrar Estat�sticas Movimento Usu�rio
			<ul>
				<li>
					<h:commandLink value="Cadastrar Movimenta��o Di�ria de Usu�rios" action="#{registroFrequenciaUsuariosBibliotecaMBean.iniciarRegistroFrequenciaUsuario}"  onclick="setAba('circulacao')"/>
				</li>
			</ul>
		</li>
	</ufrn:checkRole>
	
	<%-- Por enquanto S� quem est� fazendo empr�stimos institucionais � o setor de informa��o e referencam, por isso o link comentado 
	<li>Empr�stimos Institucionais
		<ul>
			<li>Bibliotecas/Unidades Internas
				<ul>
					<li>
						<h:commandLink value="Listar/Realizar Novo Empr�stimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('circulacao')">
							<f:param name="emprestimoParaBibliotecaExterna" value="false" />
							<f:param name="limpaLista" value="true" />
						</h:commandLink>
					</li>
				</ul>
			</li>
			<li>Bibliotecas/Unidades Externas
				<ul>
					<li>
						<h:commandLink value="Listar/Realizar Novo Empr�stimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('circulacao')">
							<f:param name="emprestimoParaBibliotecaExterna" value="true" />
							<f:param name="limpaLista" value="true" />
						</h:commandLink>
					</li>
				</ul>
			</li>
		</ul>
	</li> --%>
	
	<li> Gerenciamento de Puni��es
		<ul>
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<li>
					<h:commandLink value="Gerenciar Suspens�es" action="#{suspensaoUsuarioBibliotecaMBean.iniciarGerenciaSuspensoes}" onclick="setAba('circulacao')" />
				</li>
			</ufrn:checkRole>
			
			<%-- Pagamento de multas qualquer usu�rio de circula��o vai poder fazer --%>
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
					<h:commandLink value="Visualizar/Solicitar Reservas para um Usu�rio" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoReservasDeUmUsuario}" onclick="setAba('circulacao')" />
				</li>
				<li>
					<h:commandLink value="Visualizar Reservas de um T�tulo" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoReservasDeUmTitulo}" onclick="setAba('circulacao')" />
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
					<h:commandLink value="Listar Comunica��es de Materiais Perdidos" action="#{comunicarMaterialPerdidoMBean.listarUsuariosComMateriaisPerdidos}" onclick="setAba('circulacao')" />
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