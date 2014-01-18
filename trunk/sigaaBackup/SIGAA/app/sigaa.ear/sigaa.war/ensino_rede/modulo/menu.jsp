<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>Ensino em Rede</h2>

<h:form>
		<input type="hidden" name="aba" id="aba"/>

		<div id="operacoes-subsistema"  class="reduzido">
			
			<div id="aba_academico" class="aba">
				<ul>
					<li> Dados do Discente
						<ul>
						<li> <h:commandLink id="cadastrarDiscente" action="#{cadastroDiscenteRedeMBean.iniciarCadastrar}" value="Cadastrar Discente" onclick="setAba('aba_academico')" />	</li>						
						<li> <h:commandLink id="alterarDiscente" action="#{cadastroDiscenteRedeMBean.iniciarAlterar}" value="Alterar Discente" onclick="setAba('aba_academico')" />	</li>
						<li> <h:commandLink id="atualizarDadosPessoais" action="#{cadastroDiscenteRedeMBean.iniciarAtualizarDadosPessoais}" value="Atualizar Dados Pessoais" onclick="setAba('aba_academico')" />	</li>						
						<li> <h:commandLink id="consultarDiscente" action="#{consultarDiscenteMBean.iniciarConsultar}" value="Consultar Discente" onclick="setAba('aba_academico')" />	</li>
						</ul>
					</li>
				
					<li> P�s Convoca��o
						<ul>
						<li> <h:commandLink action="#{confirmacaoVinculoMBean.iniciarGestor}" value="Confirma��o de V�nculo" onclick="setAba('aba_academico')"/> </li>
						</ul>
					</li>				
				
					<li> V�nculo com o Programa
						<ul>
						<li> <h:commandLink id="trancarDiscente" action="#{trancamentoProgramaEnsinoRedeMBean.iniciar}" value="Trancar Discente" onclick="setAba('aba_academico')" /> </li>
						<li> <h:commandLink id="retornarTrancamento" action="#{retornarTrancamentoProgramaRedeMBean.iniciar}" value="Retornar Aluno de Trancamento" onclick="setAba('aba_academico')" /> </li>
						<li> <h:commandLink id="estorna" action="#{estornaOperacaoRedeMBean.iniciar}" value="Estornar Opera��o" onclick="setAba('aba_academico')" /> </li>
						<li> Cancelar V�nculo </li>
						</ul>
					</li>				
				
					<li> Turmas
						<ul>
						<li> <h:commandLink id="turmaRedeCadastrar" action="#{turmaRedeMBean.iniciarCadastrar}" value="Criar Turma" onclick="setAba('aba_academico')" />	</li>
						<li> <h:commandLink id="turmaRedeConsultar" action="#{turmaRedeMBean.iniciarAlterar}" value="Listar/Alterar Turma" onclick="setAba('aba_academico')" /> </li>
						<li> <h:commandLink id="alterarSituacao" action="#{alterarSituacaoMatriculaRede.iniciarConsolidacao}" value="Consolidar Turma" onclick="setAba('aba_academico')"/> </li>
						</ul>
					</li>
				
					<li> Docentes
						<ul>
							<li> <h:commandLink id="cadastrarDocente" action="#{docenteRedeMBean.iniciarCadastrar}" value="Cadastrar" onclick="setAba('aba_academico')" /> </li>
							<li> <h:commandLink id="listarAlterarDocente" action="#{docenteRedeMBean.listarDocentes}" value="Listar/Alterar" onclick="setAba('aba_academico')"/> </li>
							<li> <h:commandLink id="atualizarDadosPessoaisDocente" action="#{docenteRedeMBean.iniciarAtualizarDadosPessoais}" value="Atualizar Dados Pessoais" onclick="setAba('aba_academico')"/> </li>
							<li> <h:commandLink id="consultarHistoricoDocente" action="#{historicoDocenteRede.iniciarConsultar}" value="Consultar Hist�rico dos Docentes" onclick="setAba('aba_academico')"/></li>
							<li> <h:commandLink id="homologarDocentes" action="#{solicitacaoDocenteRedeMBean.iniciarHomologacaoDocente}" value="Homologar Docentes" onclick="setAba('aba_academico')"/> <span class="tremeluzir">(<h:outputText value="#{solicitacaoDocenteRedeMBean.numSolicitacoesPrograma}"/>)</span> </li>
						</ul>
					</li>								
				</ul>
				
			</div>			
			
			<div id="aba_pedagogico" class="aba">
				<ul>
					<li>Componentes Curriculares
						<ul>
						<li>Cadastrar</li>
						<li>Listar/Alterar</li>
						</ul>
					</li>
					
					<li>Estrutura Curricular
						<ul>
						<li>Cadastrar</li>
						<li>Listar/Alterar</li> 
						</ul>
					</li>
				</ul>
			</div>		
			
			<div id="aba_administracao" class="aba">
				<ul>
					<li> Coordena��o Unidade
						<ul>
							<li><h:commandLink id="cadastroCoordenador" action="#{coordenadorUnidadeMBean.iniciarCadastrarCoordenador}" value="Cadastrar Coordenador(a)" onclick="setAba('aba_administracao')" /></li>
							<li><h:commandLink id="listarAlterarCoordenador" action="#{coordenadorUnidadeMBean.listarCoordenadores}" value="Listar/Alterar Coordenadores" onclick="setAba('aba_administracao')"/></li>
							<li><h:commandLink id="cadastroSecretario" action="#{coordenadorUnidadeMBean.iniciarCadastrarSecretaria}" value="Cadastrar Secret�rio(a)" onclick="setAba('aba_administracao')" /> </li>
							<li><h:commandLink id="listarAlterarSecretario" action="#{coordenadorUnidadeMBean.listarSecretarios}" value="Listar/Alterar Secret�rio" onclick="setAba('aba_administracao')"/></li>

							<li><h:commandLink id="cadastrarUsuarioCoordenador" action="#{ usuarioCoordenadorUnidadeMBean.iniciar }" value="Cadastrar Usu�rio" onclick="setAba('aba_administracao')"/></li>
						</ul>
					</li>
				</ul>
				
				<ul>
					<li> Cadastrar Not�cia
						<ul>
							<li>Cadastrar</li>
						</ul>
						<ul>
							<li>Listar/Alterar</li>
						</ul>
					</li>
				</ul>				
				
				<ul>
					<li> F�rum
						<ul>
							<li><h:commandLink action="#{forum.listarForunsPrograma}" value="Gerenciar F�rum" onclick="setAba('aba_administracao')"/></li>
						</ul>
					</li>
				</ul>
			</div>					
			
			<div id="aba_relatorios" class="aba">

				<ul>
					<li> Relat�rio de Discentes
						<ul>
							<li><h:commandLink action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDiscentePorUnidade }" value="Discente por Status/Unidade" onclick="setAba('aba_relatorios')"/></li>
							<li><h:commandLink action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioContatoDiscente }" value="Contato dos Discentes" onclick="setAba('aba_relatorios')"/></li>
						</ul>
					</li>
				</ul>	

				<ul>
					<li> Relat�rio de Docentes
						<ul>
							<li><h:commandLink action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDocentes }" value="Docentes por Unidade" onclick="setAba('aba_relatorios')"/></li>
						</ul>
					</li>
				</ul>	

				<ul>
					<li> Relat�rio de Turmas
						<ul>
							<li><h:commandLink action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioTurmas }" value="Relat�rio de Docentes por Turmas" onclick="setAba('aba_relatorios')"/></li>
						</ul>
					</li>
				</ul>	

				<ul>
					<li> Relat�rio de Matriculas
						<ul>
							<li><h:commandLink action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDesempenho }" value="Desempenho por Disciplina" onclick="setAba('aba_relatorios')"/></li>
						</ul>
					</li>
				</ul>	
				
			</div>
				
		</div>
		
</h:form>
<c:set var="hideSubsistema" value="true" />

</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	abas.addTab('aba_academico', "Controle Acad�mico");
        	abas.addTab('aba_pedagogico', "Controle Pedag�gico");
        	abas.addTab('aba_administracao', "Administra��o");
        	abas.addTab('aba_relatorios', "Relat�rios");
			
		    <c:if test="${sessionScope.aba == null}">
	    		abas.activate('aba_academico');
	    	</c:if>		
		    <c:if test="${sessionScope.aba != null}">
		    	abas.activate('${sessionScope.aba}');
		    </c:if>			
			
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:remove var="aba" scope="session"/>