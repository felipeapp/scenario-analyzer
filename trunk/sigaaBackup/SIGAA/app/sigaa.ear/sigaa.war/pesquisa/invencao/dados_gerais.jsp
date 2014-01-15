<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
p.descricao {
	margin: 5px 100px;
	text-align: center;
	font-style: italic;
}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Notificação de Invenção &gt; Dados Básicos</h2>

	<h:form id="form">
		
		<table class="formulario" width="100%">
			<caption>Descrição da Invenção</caption>
			<tr>
				<td colspan="2" class="subFormulario"> Dados Básicos </td>
			</tr>
			<tr>
				<th align="right"><b>Código:</b></th>
				<td>
					<c:choose>
						<c:when test="${not empty invencao.obj.codigo}">
							${ invencao.obj.codigo }
						</c:when>
						<c:otherwise>
							A ser gerado automaticamente.
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Centro:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{invencao.obj.centro.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{invencao.centrosCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Tipo:</th>
				<td><a4j:region>
					<h:selectOneMenu id="tipo" value="#{invencao.obj.tipo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoInvencao.allCombo}"/>
						<a4j:support event="onchange" reRender="label,select"/>
					</h:selectOneMenu>
					</a4j:region>
				</td>
			</tr>
			
			<tr>
				<th>
					<h:panelGroup id="label">
						<c:if test="${invencao.obj.tipo.id == 1}">
							Categoria:
						</c:if>
					</h:panelGroup>
				</th>
				<td>
					<h:panelGroup id="select">
						<c:if test="${invencao.obj.tipo.id == 1}">
							<h:selectOneMenu id="categoria" value="#{invencao.obj.categoriaPatente}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{tipoInvencao.categoriasCombo}"/>
							</h:selectOneMenu>
						</c:if>
					</h:panelGroup>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario"> Palavras-chave </td>
			</tr>
			<tr>
				<td colspan="2">
				<div class="descricaoOperacao">
					<p>
						Informar pelo menos 6 palavras-chave em inglês e 6 em português, separadas por vírgulas.
					</p>
				</div> 
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Palavras-chave (Português):</th>
				<td>
					<h:inputText id="palavrasChavePortugues" value="#{invencao.obj.palavrasChavePortugues}" size="50" maxlength="255"/>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Palavras-chave (Inglês):</th>
				<td>
					<h:inputText id="palavrasChaveIngles" value="#{invencao.obj.palavrasChaveIngles}" size="50" maxlength="255"/>
				</td>
			</tr>			
			
			<tr>
				<td colspan="2" class="subFormulario"> Área de Conhecimento </td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Grande Área:</th>
				<td>
					<h:selectOneMenu id="grandeArea"
						value="#{invencao.grandeArea.id}"
						readonly="#{invencao.readOnly}" style="width: 70%;" 
						valueChangeListener="#{invencao.changeGrandeArea}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{area.allGrandesAreasCombo}"/>
						<a4j:support event="onchange" reRender="area" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Área:</th>
				<td>
					<h:selectOneMenu id="area"
						value="#{invencao.area.id}"
						readonly="#{invencao.readOnly}" style="width: 70%;" 
						valueChangeListener="#{invencao.changeArea}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE ANTES UMA GRANDE ÁREA --"/>
						<f:selectItems value="#{invencao.areas}"/>
						<a4j:support event="onchange" reRender="subArea" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th >Sub-Área:</th>
				<td>
					<h:selectOneMenu id="subArea"
						value="#{invencao.subarea.id}"
						readonly="#{invencao.readOnly}" style="width: 70%;" 
						valueChangeListener="#{invencao.changeSubArea}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE ANTES UMA ÁREA --"/>
						<f:selectItems value="#{invencao.subareas}"/>
						<a4j:support event="onchange" reRender="especialidade" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th >Especialidade:</th>
				<td>
					<h:selectOneMenu id="especialidade"
						value="#{invencao.especialidade.id}"
						readonly="#{invencao.readOnly}" style="width: 70%;" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE ANTES UMA SUB-ÁREA --"/>
						<f:selectItems value="#{invencao.especialidades}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<c:if test="${not invencao.obj.ativo}">
				<tr>
					<th>Ativo:</th>
					<td><h:selectBooleanCheckbox value="#{invencao.obj.ativo}"/> </td>
				</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" immediate="true"/>
						<h:commandButton value="Avançar >>" action="#{invencao.submeterDadosGerais}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
