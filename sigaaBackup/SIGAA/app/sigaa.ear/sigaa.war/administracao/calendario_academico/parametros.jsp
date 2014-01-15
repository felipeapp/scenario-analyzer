<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	<h2 class="title"><ufrn:subSistema /> > Calendário Acadêmico - Escolha de Parâmetros</h2>
	<h:form id="form">
		<table class="formulario" width="95%">
			<caption>Escolha os Parâmetros</caption>
			<tr>
				<th ${calendario.podeAlterarUnidade ? "" : "style='font-weight: bold;'"}>Unidade Responsável:</th>
				<td>
				<h:selectOneMenu id="unidades" onchange="submit()" rendered="#{calendario.podeAlterarUnidade}"
					valueChangeListener="#{calendario.selecionarGestora}"
					value="#{calendario.obj.unidade.id}">
					<f:selectItems value="#{unidade.allGestorasAcademicasCombo}" />
				</h:selectOneMenu>
				<h:outputText value="#{calendario.obj.unidade.nome}" rendered="#{!calendario.podeAlterarUnidade}" />
				</td>
			</tr>
				<tr>
					<th  ${calendario.podeAlterarNivel ? "" : "style='font-weight: bold;'"}>Nível de Ensino:</th>
					<td><h:selectOneMenu onchange="submit()" rendered="#{calendario.podeAlterarNivel}"
						valueChangeListener="#{calendario.selecionarNivel}" id="niveis">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{calendario.comboNiveis}" />
					</h:selectOneMenu>
					<c:if test="${!calendario.podeAlterarNivel}">
					${calendario.obj.nivelDescr}
					</c:if>
					</td>
				</tr>
				<tr>
					<th  ${calendario.podeAlterarModalidade ? "" : "style='font-weight: bold;'"}>Modalidade de Ensino:</th>
					<td>
						<c:if test="${calendario.podeAlterarModalidade}">
						<h:selectOneMenu id="modalidade" valueChangeListener="#{calendario.carregarCursosPorModalidade}"
							   onchange="submit()" value="#{calendario.obj.modalidade.id}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{modalidadeEducacao.allCombo}" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Se não escolher uma modalidade serão
						exibidos os calendários independente da modalidade</ufrn:help>
						</c:if>
						<h:outputText value="#{(calendario.obj.modalidade.descricao == null ? 'Nenhuma' : '' )}" rendered="#{!calendario.podeAlterarModalidade}" />
					</td>
				</tr>
				<tr>
					<th  ${calendario.podeAlterarConvenio ? "" : "style='font-weight: bold;'"}>Convênio Acadêmico:</th>
					<td>
						<c:if test="${calendario.podeAlterarConvenio}">
						<h:selectOneMenu id="convenio" valueChangeListener="#{calendario.carregarCursosPorConvenio}"
							 onchange="submit()" value="#{calendario.obj.convenio.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{convenioAcademico.allCombo}" />
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Se não escolher um convênio serão
						exibidos os calendários independente do convênio</ufrn:help>
						</c:if>
						<h:outputText value="#{ (calendario.obj.convenio.descricao == null ? 'Nenhum' : '' )}" rendered="#{!calendario.podeAlterarConvenio}" />
					</td>
				</tr>
				<tr>
					<th ${calendario.podeAlterarCurso ? "" : "style='font-weight: bold;'"}>Curso:</th>
					<td>
						<h:selectOneMenu value="#{calendario.obj.curso.id}"	id="curso" rendered="#{calendario.podeAlterarCurso}"
								valueChangeListener="#{calendario.carregarModalidadePorCurso}" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{calendario.comboCursos}" />
						</h:selectOneMenu>
						<h:outputText value="#{ (calendario.obj.curso.nome == null ? 'Todos' : '' )}" rendered="#{!calendario.podeAlterarCurso}" />
					</td>
				</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendario.cancelar}" id="cancelar" immediate="true"/>
						<h:commandButton value="Ver Calendários >> " id="submParams" action="#{calendario.submeterParametros}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>


<script type="text/javascript">
<!--
function zerarConvenio() {
}
//-->
</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
