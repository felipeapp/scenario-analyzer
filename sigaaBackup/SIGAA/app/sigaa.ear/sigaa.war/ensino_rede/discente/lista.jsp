<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName = "selecionaDiscenteMBean" />

<h2> <ufrn:subSistema /> &gt; Buscar Discente</h2>

	<h:form  id="formulario">
		<a4j:keepAlive beanName="selecionaDiscenteMBean"></a4j:keepAlive>	
		<table class="formulario" style="width:58%;">
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox value="#{selecionaDiscenteMBean.parametros.checkCpf}" styleClass="noborder" id="checkCpf" /></td>
				<th style="text-align: left">
					<label for="checkCpf" onclick="$('formulario:checkCpf').checked = !$('formulario:checkCpf').checked;">CPF:</label>
				</th>
				<td onclick="marcaCheckBox('formulario:checkCpf');">
					<h:inputText value="#{selecionaDiscenteMBean.valores.valorCpf}" converter="convertCpf" size="14" id="cpfDiscente" maxlength="14" 
						onkeypress="return formataCPF(this, event, null);" onblur="formataCPF(this, event, null)">
						<f:converter converterId="convertCpf" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{selecionaDiscenteMBean.parametros.checkNome}" styleClass="noborder" id="checkNome" /></td>
				<th style="text-align: left">
					<label for="checkNome" onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">Nome:</label>
				</th>
				<td>
					<h:inputText value="#{selecionaDiscenteMBean.valores.valorNome}" size="60" id="nomeDiscente" maxlength="120"
						onfocus="getEl('formulario:checkNome').dom.checked = true;" />
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{selecionaDiscenteMBean.parametros.checkCampus}" styleClass="noborder" id="checkCampus" 
					rendered="#{ !selecionaDiscenteMBean.restringeCampus }"/></td>
				<th style="text-align: left" class="${ selecionaDiscenteMBean.restringeCampus ? 'rotulo' : '' }">
					<label for="checkCampus" onclick="$('formulario:checkCampus').checked = !$('formulario:checkCampus').checked;">Campus:</label>
				</th>
				<td>
					<h:selectOneMenu value="#{selecionaDiscenteMBean.valores.valorIdCampus}" style="width: 40%;" 
						onfocus="getEl('formulario:checkCampus').dom.checked = true;" rendered="#{ !selecionaDiscenteMBean.restringeCampus }">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{selecionaDiscenteMBean.campusCombo}" id="campusCombo"/>
					</h:selectOneMenu>		
					<h:outputText value="#{ selecionaDiscenteMBean.campusRestrito.instituicao.nome } - #{ selecionaDiscenteMBean.campusRestrito.nome }" 
						rendered="#{ selecionaDiscenteMBean.restringeCampus }"/>		
				</td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: left" class="rotulo">Programa em Rede:</th>
				<td>
					<h:outputText value="#{ selecionaDiscenteMBean.programaRede.descricao }"/>		
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{selecionaDiscenteMBean.buscarDiscentes}" value="Buscar" id="buscar" />
					<h:commandButton action="#{buscaDiscenteGraduacao.cancelar}" value="Cancelar" id="cancelar" onclick="return confirm('Deseja cancelar a operação?');" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br/>
	<c:if test="${not empty selecionaDiscenteMBean.resultadosBusca}">	
		<h:form>
			
			<div class="infoAltRem" style="width: 80%">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Discente
			</div>
			
			<table class="listagem" style="width: 80%">
			  <caption>Discente(s) Encontrado(s) (${fn:length(selecionaDiscenteMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th style="text-align: center;"> CPF </th>
						<th width="70%">Nome</th>
						<th>Status</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="grupoCampus" value=""/>
					<c:forEach var="linha" items="#{selecionaDiscenteMBean.resultadosBusca}" varStatus="status">
						<c:if test="${ grupoCampus != linha.dadosCurso.campus.sigla}">
							<c:set var="grupoCampus" value="${linha.dadosCurso.campus.sigla }"/>
							<tr>
								<td class="subFormulario" colspan="4">${linha.dadosCurso.campus.instituicao.nome} - ${linha.dadosCurso.campus.nome}</td>
							</tr>
						</c:if>
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td style="text-align: center;">${linha.pessoa.cpfCnpjFormatado} </td>
							<td> 
								${linha.pessoa.nome }
							</td>
							<td> 
								${linha.status.descricao }
							</td>
							
							<td align="right" width="2%">
								<h:commandButton image="/img/seta.gif" actionListener="#{selecionaDiscenteMBean.escolheDiscente}" title="Selecionar Discente"  id="selecionarDiscente">
									<f:attribute name="idDiscente" value="#{linha.id}" />
								</h:commandButton>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</h:form>
	</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>