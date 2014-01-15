<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Identificação de Secretários</h2>

	<h:form id="form">

		<table class="formulario">
			<caption class="formulario">Identificar Secretário de ${secretariaUnidade.tipoSecretaria}</caption>
			<tr>
				<th class="required">Usuário:</th>
				<td>
					<h:inputHidden id="idUsuario" value="#{secretariaUnidade.obj.usuario.id}"/>
					<h:inputText id="nomeUsuario" value="#{secretariaUnidade.obj.usuario.nome}" size="70" onkeyup="CAPS(this)"
						disabled="#{secretariaUnidade.readOnly}" readonly="#{secretariaUnidade.readOnly}" />

					<ajax:autocomplete source="form:nomeUsuario" target="form:idUsuario"
							baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
							indicator="indicatorUsuario" minimumCharacters="3"
							parser="new ResponseXmlToHtmlListParser()" parameters="${((secretariaUnidade.programaPos or secretariaUnidade.coordenacaoLato)?'':'servidor=${secretariaUnidade.somenteServidores}')}"/>

					<span id="indicatorUsuario" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					<c:if test="${!(secretariaUnidade.programaPos or secretariaUnidade.coordenacaoLato)}">
						<ufrn:help><i>Apenas usuários com vínculo de Servidor serão listados</i></ufrn:help>
					</c:if>
				</td>
			</tr>
			<c:if test="${secretariaUnidade.coordenacao}">
				<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu id="coordenacao" value="#{secretariaUnidade.obj.curso.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ curso.allCursoGraduacaoCombo}"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</c:if>
			<c:if test="${secretariaUnidade.coordenacaoLato}">
				<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<ufrn:subSistema teste="lato">
						<h:selectOneMenu id="gestorLato" value="#{secretariaUnidade.obj.curso.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ curso.allCursoEspecializacaoAceitoCombo}"/>
						</h:selectOneMenu>
					</ufrn:subSistema>
					<ufrn:subSistema teste="not lato">
						<h:selectOneMenu id="coordenadorLato" value="#{secretariaUnidade.obj.curso.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ curso.allCoordenadorCombo}"/>
						</h:selectOneMenu>
					</ufrn:subSistema>
				</td>
				</tr>
			</c:if>
			<c:if test="${secretariaUnidade.coordenacaoTecnico}">
				<tr>
					<th class="required">Escola:</th>
					<td>
						<h:selectOneMenu id="coordenadorTecnico" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ unidade.allEscolaCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${secretariaUnidade.departamento}">
				<tr>
				<th>Departamento:<span class="required">&nbsp;</span></th>
				<td>
					<h:selectOneMenu id="departamento" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ unidade.allDeptosEscolasCombo }"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</c:if>
			<c:if test="${secretariaUnidade.centro}">
				<tr>
				<th>Centro:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ unidade.allCentroCombo }"/>
					</h:selectOneMenu>
					<span class="required">&nbsp;</span>
				</td>
				</tr>
			</c:if>
			<c:if test="${secretariaUnidade.programaPos}">
				<tr>
				<th class="obrigatorio">Programa:</th>
				<td>
					<h:selectOneMenu id="programa" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
					</h:selectOneMenu>					
				</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{secretariaUnidade.confirmButton}"	action="#{secretariaUnidade.cadastrar}" id="btncadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{secretariaUnidade.cancelar}" immediate="true" id="btncancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<script type="text/javascript">$('form:nomeUsuario').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
