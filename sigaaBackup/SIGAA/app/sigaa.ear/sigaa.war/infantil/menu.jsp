<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>M�dulo Ensino Infantil e Fundamental</h2>
	
	<h:form id="menuInfantilFundamentalForm">
		<input type="hidden" name="aba" id="aba"/>
		
		<div id="operacoes-subsistema"  class="reduzido">
			 
			<div id="principal" class="aba">
				<ul>
					<li> Cadastro de Alunos
						<ul>
							<li> <h:commandLink value="Cadastrar Novo Aluno" action="#{discenteInfantilMBean.iniciar}"></h:commandLink> </li>
							<li> <h:commandLink value="Atualizar Dados do Aluno" action="#{discenteInfantilMBean.atualizar}"></h:commandLink> </li>
						</ul>
					</li>
	
					<li>Docentes Externos (Estagi�rios)
						<ul>
							<li><h:commandLink action="#{docenteExterno.popular}" onclick="setAba('permissao')" >Cadastrar</h:commandLink></li>
							<li><h:commandLink action="#{docenteExterno.iniciarBusca}" onclick="setAba('permissao')" >Listar/Alterar</h:commandLink></li>
						</ul>
					</li>
					
					<li> Matr�cula
						<ul>
							<li> <h:commandLink value="Matricular Alunos" action="#{matriculaInfantilMBean.iniciar}"></h:commandLink> </li>
							<li> <h:commandLink	action="#{alteracaoStatusMatricula.iniciar }" value="Alterar Status de Matr�cula"/> </li>
						</ul>
					</li>
	
									
					<li> Avalia��o
						<ul>
							<li> <h:commandLink value="Registrar Evolu��o da Crian�a" action="#{ registroEvolucaoCriancaMBean.iniciar }"></h:commandLink> </li>
							<li> <h:commandLink value="Ver Registros da Evolu��o da Crian�a" action="#{ registroEvolucaoCriancaMBean.iniciarVisualizacaoRegistros }"></h:commandLink> </li>
							<li> <h:commandLink value="Cadastrar/Alterar Formul�rio de Evolu��o" action="#{formularioEvolucaoCriancaMBean.listar}"></h:commandLink> </li>
						</ul>
					</li>
					
					<li> Turmas
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{turmaInfantilMBean.iniciar}"/> </li>
							<li> <h:commandLink value="Listar/Alterar" action="#{turmaInfantilMBean.listar}"/> </li>
						</ul>
					</li>
					
					<li> Renda Familiar
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{rendaFamiliarMBean.preCadastrar}"/> </li>
							<li> <h:commandLink value="Listar/Alterar" action="#{rendaFamiliarMBean.listar}"/> </li>
						</ul>
					</li>

					<li> Consolida��o Turma
						<ul>
							<li> <h:commandLink value="Consolidar" action="#{consolidacaoTurmaInfantilMBean.preConsolidar}"/> </li>
						</ul>
					</li>
					
					<li> Discente com NEE
						<ul>
							<li> <h:commandLink id="cadastrarDiscenteNEE" value="Solicitar Apoio a CAENE" action="#{solicitacaoApoioNee.preCadastrarDiscenteNee}"/> </li>
							<li> <h:commandLink id="AlterarDiscenteNEE" value="Alterar Solicita��o de Apoio" action="#{solicitacaoApoioNee.listarSolicitacoesDiscente}"/> </li>
							<li> <h:commandLink id="parecerNEE" value="Solicita��es Enviadas para CAENE" action="#{solicitacaoApoioNee.listaAllSolicitacoes}"/> </li>
							<li> <a href="${linkPublico.urlDownload}/manual_nee_solicitacao_apoio.pdf" target="_blank" id="manualNEE">Manual de Solicita��o de Apoio</a> </li>
						</ul>
					</li>
					
				</ul>
			</div>
			
			
			<div id="relatorios" class="aba">
				<ul>
					<li> Alunos
						<ul>
							<li> <h:commandLink value="Relat�rio do Registro de Evolu��o da Crian�a" action="#{registroEvolucaoCriancaMBean.iniciarRelatorioRegistroEvolucao}" /> </li>
						</ul>
					</li>
				</ul>
			</div>
			
		</div>
		
		<script>
			var Abas = function() {
				return {
				    init : function(){
				        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
					        abas.addTab('principal', "Alunos");
							abas.addTab('relatorios', "Relat�rios")
							abas.activate('principal');
				    }
			    }
			}();
			YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
			function setAba(aba) {
				document.getElementById('aba').value = aba;
			}
		</script>
	
	</h:form>

	<div class="linkRodape">
	    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
	</div>

</f:view>




<c:set var="hideSubsistema" value="true" />
<c:remove var="aba" scope="session"/>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>