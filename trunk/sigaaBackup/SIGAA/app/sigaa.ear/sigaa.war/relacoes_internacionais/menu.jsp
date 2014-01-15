<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /></h2>

<h:form>
		<input type="hidden" name="aba" id="aba"/>

		<div id="operacoes-subsistema"  class="reduzido">

			<div id="documentos" class="aba">
				<ul>
					<li> Hist�rico
						<ul>
							<li> <h:commandLink value="Tradu��o de Hist�rico por Discente" action="#{historicoTraducaoMBean.buscarDiscente}" onclick="setAba('documentos')"/></li>
						</ul>
					</li>
					<li> Curr�culo
						<ul>
							<li> <h:commandLink value="Tradu��o de Componente(s) por Estrutura Curricular" action="#{traducaoCurriculoMBean.iniciar}" onclick="setAba('documentos')"/> </li>
						</ul>
					</li>
					<li> Elementos do Documento
						<ul>
							<li> <h:commandLink value="Tradu��o de Componente Curricular" action="#{componenteTraducaoMBean.iniciar}" onclick="setAba('documentos')"/> </li>
							<li> 
								<h:commandLink value="Tradu��o de Curso" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="curso" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Modalidade de Educa��o" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="modalidadeEducacao" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Grau Acad�mico" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="grauAcademico" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Habilita��o" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="habilitacao" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de �nfase" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="enfase" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Forma de Ingresso" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="formaIngresso" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Situa��o de Matr�cula" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="situacaoMatricula" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Programa" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="unidade" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de �rea de Concentra��o" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="areaConcentracao" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Turma" action="#{turmaTraducaoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="turma" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de �ndice Acad�mico" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="indiceAcademico" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Participa��o do ENADE" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="participacaoEnade" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Tipo de Movimenta��o do Aluno" action="#{traducaoElementoMBean.iniciar}" onclick="setAba('documentos')">
								 	<f:param value="tipoMovimentacaoAluno" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							
						</ul>	
					</li>
					<li> Documentos
						<ul>
							<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('documentos')"/> </li>
							<li> <h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('documentos')"/> </li>
							<li> <h:commandLink	action="#{ historico.buscarDiscenteHistoricoEmenta }"	value="Emitir Hist�rico com Ementas Traduzidas" onclick="setAba('documentos')"/> </li>
							<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declara��o de V�nculo/Cadastro" onclick="setAba('documentos')"/> </li>
						</ul>
					</li>
					<li> Alunos
						<ul>
							<li> 
								<h:commandLink value="Tradu��o de Mobilidade Estudantil" action="#{elementosDiscenteTraducaoMBean.buscarDiscente}" onclick="setAba('documentos')">
								 	<f:param value="mobilidadeEstudantil" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
							<li> 
								<h:commandLink value="Tradu��o de Observa��es do Discente" action="#{elementosDiscenteTraducaoMBean.buscarDiscente}" onclick="setAba('documentos')">
								 	<f:param value="observacaoDiscente" name="entidadeOperacao"/>
								</h:commandLink>
							</li>
						</ul>
					</li>
				</ul>
			</div>

			<div id="administracao" class="aba">
				<ul>
					<li> Entidade do Hist�rico
						<ul>
							<li> <h:commandLink action="#{entidadeTraducaoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')"/> </li>
							<li> <h:commandLink action="#{entidadeTraducaoMBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')"/></li>
						</ul>
					</li>
					<li> Elemento do Hist�rico
						<ul>
							<li> <h:commandLink action="#{itemTraducaoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')"/> </li>
							<li> <h:commandLink action="#{itemTraducaoMBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')"/></li>
						</ul>
					</li>
					<li> Constantes para Internacionaliza��o
						<ul>
							<li> <h:commandLink action="#{constanteTraducaoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')"/> </li>
							<li> <h:commandLink action="#{constanteTraducaoMBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')"/></li>
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
        	abas.addTab('documentos', "Documentos");
        	abas.addTab('administracao', "Administra��o");
        	<c:if test="${sessionScope.aba != null && sessionScope.aba != ''}">
		    	abas.activate('${sessionScope.aba}');
		    </c:if>
		    <c:if test="${sessionScope.aba == null}">
				abas.activate('documentos');
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