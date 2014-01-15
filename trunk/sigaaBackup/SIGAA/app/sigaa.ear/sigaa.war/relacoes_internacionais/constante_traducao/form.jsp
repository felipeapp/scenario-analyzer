<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="constanteTraducaoMBean" />
	<h2><ufrn:subSistema /> > Tradu��o de Constantes para Internacionaliza��o</h2> 
	
	<div class="descricaoOperacao">
		Utilize esta opera��o para realizar a tradu��o das constantes de textos utilizados na emiss�o de documentos oficiais da institui��o. 
		Para realizar a internacionaliza��o dos elementos, informe a tradu��o nos valores destinados a cada idioma. 
	</div>
	
	<h:form>
		<c:if test="${ constanteTraducaoMBean.obj.id > 0 }">
			<table class="visualizacao" style="width: 80%">
				<tr>
					<th width="22%" style="text-align: right;">Entidade de Tradu��o:</th>
					<td style="text-align: left;">${constanteTraducaoMBean.obj.entidade.nome}</td>
				</tr>
				<tr>
					<th width="22%" style="text-align: right;">Constante:</th>
					<td style="text-align: left;">${constanteTraducaoMBean.obj.constante}</td>
				</tr>
			</table>
		</c:if>	
		<br/><br/>	
	
		<table class="formulario" width="80%">
			<c:choose>
			<c:when test="${ constanteTraducaoMBean.obj.id > 0 }">
				<caption>Internacionaliza��o de de Constantes</caption>
			</c:when>
			<c:otherwise>
				<caption>Cadastro de Constantes para Internacionaliza��o</caption>
				<tr>
					<td>
					<table class="subFormulario" style="width: 60%">
						<tbody>
							<tr>
								<td width="20%" class="obrigatorio"><label>Entidade:</label></td>
								<td>
									<h:selectOneMenu id="select_entidade" value="#{constanteTraducaoMBean.entidadeTraducao.id}">
										<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
										<f:selectItems value="#{entidadeTraducaoMBean.allCombo}"/>
									</h:selectOneMenu>
								</td>	
							</tr>
							<tr>
								<td width="20%" class="obrigatorio"><label>Constante:</label></td>
								<td><h:inputText size="60" maxlength="100" value="#{constanteTraducaoMBean.constante}" id="input_constante"/></td>
							</tr>
						</tbody>
					</table>
					</td>
				</tr>		
			</c:otherwise>	
			</c:choose>
			
			<tbody>		
				<tr class="linhaCinza">
					<th style="text-align:left; padding-left: 5px; font-weight: bold">Tradu��o</th>
				</tr>	
				<c:forEach items="#{constanteTraducaoMBean.listaConstanteTraducao}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaImpar' : 'linhaPar' }">
						<td>
							<h:outputText value="#{item.descricaoIdioma}:"/><br/>
							<h:inputTextarea value="#{item.valor}" cols="100" rows="5"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{constanteTraducaoMBean.confirmButton}" action="#{constanteTraducaoMBean.cadastrar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{constanteTraducaoMBean.cancelarForm}" />
				</td>
			</tr>
		</table>	
		
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>