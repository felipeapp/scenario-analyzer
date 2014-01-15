<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Substituição de Secretarias</h2>

	<h:form id="form">

		<table class="formulario" width="70%">
			<caption class="formulario">Identificar Secretário de ${secretariaUnidade.tipoSecretaria}</caption>
			<tbody>
				<tr>
					<td colspan="2" class="subFormulario">Secretário(a) a ser Substituído(a)</td>
				</tr>
			
				<c:if test="${secretariaUnidade.coordenacao}">
				<tr>
					<th class="obrigatorio">Curso:</th>
					<td>
						<h:selectOneMenu id="coordenacao" value="#{secretariaUnidade.obj.curso.id}" style="width: 80%"
								valueChangeListener="#{secretariaUnidade.carregarSecretarios}" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ curso.allCursoGraduacaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
				
				<c:if test="${secretariaUnidade.coordenacaoLato}">
				<tr>
					<th width="15%" class="obrigatorio">Curso:</th>
					<td>
							<ufrn:subSistema teste="lato">
								<h:selectOneMenu id="gestorLato" value="#{secretariaUnidade.obj.curso.id}" 
										style="width: 80%" valueChangeListener="#{secretariaUnidade.carregarSecretarios}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{curso.allCursoEspecializacaoAceitoCombo}" />
									<a4j:support event="onchange" reRender="secretarioAtual" />
								</h:selectOneMenu>
							</ufrn:subSistema>

							<ufrn:subSistema teste="not lato">
								<h:selectOneMenu id="coordenadorLato" value="#{secretariaUnidade.obj.curso.id}" 
										style="width: 80%" valueChangeListener="#{secretariaUnidade.carregarSecretarios}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{curso.allCoordenadorCombo}" />
									<a4j:support event="onchange" reRender="secretarioAtual" />
								</h:selectOneMenu>
							</ufrn:subSistema>
					</td>
				</tr>
				</c:if>
			
				<c:if test="${secretariaUnidade.departamento}">
				<tr>
					<th class="obrigatorio">Departamento:</th>
					<td>
						<h:selectOneMenu id="departamento" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%"
								valueChangeListener="#{secretariaUnidade.carregarSecretarios}" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ unidade.allDeptosEscolasCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
			
				<c:if test="${secretariaUnidade.centro}">
				<tr>
					<th class="obrigatorio">Centro:</th>
					<td>
						<h:selectOneMenu id="departamento" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%"
								valueChangeListener="#{secretariaUnidade.carregarSecretarios}" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ unidade.allCentroCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
			
				<c:if test="${secretariaUnidade.programaPos}">
				<tr>
					<th class="obrigatorio">Programa:</th>
					<td>
						<h:selectOneMenu id="programa" value="#{secretariaUnidade.obj.unidade.id}" style="width: 80%"
								valueChangeListener="#{secretariaUnidade.carregarSecretarios}" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ unidade.allProgramaPosCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
				
				<c:if test="${secretariaUnidade.coordenacaoTecnico}">
					<tr>
						<th width="5%" class="required">Escola</th>
						<td>
							<h:selectOneMenu id="coordenadorTecnico" value="#{secretariaUnidade.obj.unidade.id}"
									valueChangeListener="#{secretariaUnidade.carregarSecretarios}" onchange="submit()">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{ unidade.allEscolaCombo }"/>
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th class="obrigatorio">Secretário(a) Atual:</th>
					<td>
						<h:selectOneMenu id="secretarioAtual" value="#{secretariaUnidade.secretarioAntigo.id}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ secretariaUnidade.secretarias }" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">Data de Fim:&nbsp;</th>
					<td width="15%">
						<t:inputCalendar value="#{secretariaUnidade.secretarioAntigo.fim}" size="10"
								maxlength="10" renderAsPopup="true"	renderPopupButtonAsImage="true" id="datafim"
								onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					</td>
				</tr>

				<tr>
					<td colspan="2" class="subFormulario">Novo Secretário</td>
				</tr>

				<tr>
					<th class="obrigatorio">Usuário:</th>
					<td>
						<h:inputHidden id="idUsuario" value="#{secretariaUnidade.obj.usuario.id}" />
						<h:inputText id="nomeUsuario" value="#{secretariaUnidade.obj.usuario.nome}" size="70" onkeyup="CAPS(this)"
								disabled="#{secretariaUnidade.readOnly}" readonly="#{secretariaUnidade.readOnly}" />
						<ajax:autocomplete source="form:nomeUsuario" target="form:idUsuario"
								baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
								indicator="indicatorUsuario" minimumCharacters="3"
								parser="new ResponseXmlToHtmlListParser()" 
								parameters="${(secretariaUnidade.programaPos or secretariaUnidade.coordenacaoLato?'':'servidor=true')}" />

						<span id="indicatorUsuario" style="display:none; ">
							<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..." /> 
						</span>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{secretariaUnidade.confirmButton}"
								action="#{secretariaUnidade.cadastrar}" id="btncadastrar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}"
								action="#{secretariaUnidade.cancelar}" id="btncancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br />
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>

	<script type="text/javascript">$('form:nomeUsuario').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
