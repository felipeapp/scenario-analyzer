<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> &gt; Buscar Docentes</h2>

	<h:form  id="formulario" rendered="#{selecionaDocenteRedeMBean.exibirForm }">
	
		<table class="formulario" style="width:50%;">
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<td width="2%"><h:selectBooleanCheckbox value="#{selecionaDocenteRedeMBean.parametros.checkNome}" styleClass="noborder" id="checkNome" /></td>
				<th style="text-align: left"  width="10%">
					<label for="checkNome" onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">Nome:</label>
				</th>
				<td>
					<h:inputText value="#{selecionaDocenteRedeMBean.valores.valorNome}" size="40"  id="nomeDocente" onfocus="getEl('formulario:checkNome').dom.checked = true;" />
				</td>
			</tr>
			<tr>
				<td width="2%"><h:selectBooleanCheckbox value="#{selecionaDocenteRedeMBean.parametros.checkCpf}" styleClass="noborder" id="checkCpf" /></td>
				<th style="text-align: left"  width="10%">
					<label for="checkCpf" onclick="$('formulario:checkCpf').checked = !$('formulario:checkCpf').checked;">CPF:</label>
				</th>
				<td>
					<h:inputText value="#{selecionaDocenteRedeMBean.valores.valorCpf}" size="14" id="cpfDocente" maxlength="14" 
						onfocus="getEl('formulario:checkCpf').dom.checked = true;" onkeypress="return formataCPF(this, event, null);">
						<f:converter converterId="convertCpf"/>
						<f:param name="type" value="cpf" />				
				 	</h:inputText>
				</td>
			</tr>
			<tr>
				<c:if test="${selecionaDocenteRedeMBean.exibirComboCampus}">
					<td><h:selectBooleanCheckbox value="#{selecionaDocenteRedeMBean.parametros.checkCampus}" styleClass="noborder" id="checkCampus" /></td>
					<th style="text-align: left">
						<label for="checkCampus" onclick="$('formulario:checkCampus').checked = !$('formulario:checkCampus').checked;">Campus:</label>
					</th>
					<td>
						<h:selectOneMenu value="#{selecionaDocenteRedeMBean.valores.valorIdCampus}" style="width: 40%;" onfocus="getEl('formulario:checkCampus').dom.checked = true;">
							<f:selectItem itemLabel="-- TODOS --" itemValue="0"  />
							<f:selectItems value="#{selecionaDocenteRedeMBean.campusCombo}" id="campusCombo"/>
						</h:selectOneMenu>				
					</td>
				</c:if>
				<c:if test="${!selecionaDocenteRedeMBean.exibirComboCampus}">
					<td></td>
					<th style="text-align: left">
						<b>Campus:</b>
					</th>
					<td>
						<h:outputText id="campusCoordenadorUnidade" value="#{selecionaDocenteRedeMBean.campus.descricao}"/>			
					</td>
				</c:if>
			</tr>			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{selecionaDocenteRedeMBean.buscarDocentes}" value="Buscar" id="buscar" />
					<h:commandButton action="#{buscaDiscenteGraduacao.cancelar}" value="Cancelar" id="cancelar" onclick="return confirm('Deseja cancelar a operação?')" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	</h:form>
	
	<c:if test="${not empty selecionaDocenteRedeMBean.resultadosBusca}">	
		<h:form>
			
			<div class="infoAltRem" style="width:80%">
				<c:if test="${!selecionaDocenteRedeMBean.consultar}">				
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar  Docente<br />
				</c:if>
				<c:if test="${selecionaDocenteRedeMBean.consultar}">				
					<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" />: Visualizar  Docente<br />			
				</c:if>
			</div>
			
			<table class="listagem" style="width:80%">
			  <caption>Docente(s) Encontrado(s) (${fn:length(selecionaDocenteRedeMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th width="70%">Nome</th>
						<th>Tipo</th>
						<th>Situação</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="cies" value="0"/>
					<c:forEach var="linha" items="#{selecionaDocenteRedeMBean.resultadosBusca}" varStatus="status">
						<c:if test="${cies != linha.dadosCurso.campus.id}">
							<tr>
								<td colspan="6" style="background-color: #C8D5EC">
									<c:set var="cies" value="#{linha.dadosCurso.campus.id}"/>
									<b>${linha.dadosCurso.campus.descricao}</b>
								</td>
							</tr>		
						</c:if>
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td> 
								${linha.pessoa.nome }
							</td>
							<td> 
								${linha.tipo.descricao }
							</td>
							<td> 
								${linha.situacao.descricao }
							</td>
							
							<td align="right" width="2%">
								<c:if test="${!selecionaDocenteRedeMBean.consultar}">
									<h:commandButton image="/img/seta.gif" actionListener="#{selecionaDocenteRedeMBean.escolheDocente}" title="Selecionar Docente"  id="selecionarDocente">
										<f:attribute name="idDocente" value="#{linha.id}" />
									</h:commandButton>
								</c:if>
								<c:if test="${selecionaDocenteRedeMBean.consultar}">
									<h:commandButton image="/img/buscar.gif" actionListener="#{selecionaDocenteRedeMBean.escolheDocente}" title="Visualizar Docente"  id="selecionarDocente">
										<f:attribute name="idDocente" value="#{linha.id}" />
									</h:commandButton>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</h:form>
	</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>