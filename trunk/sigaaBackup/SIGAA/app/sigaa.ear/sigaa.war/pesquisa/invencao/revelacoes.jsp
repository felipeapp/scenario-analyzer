<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Notificação de Invenção &gt; Revelações</h2>

	<h:form id="form">
		
		<table class="formulario" width="90%">
			<caption>Revelações</caption>
			
			<tr>
				<td colspan="2" class="subFormulario"> Revelação da Invenção </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Caso já tenha sido realizada ou se está para haver revelação referente ao objeto da invenção,
							informar o tipo de publicação, a data da publicação (ou data provável) e o local de publicação.
						</p>
					</div> 
				</td>
			</tr>
			
			<tr>
				<th >Tipo de Publicação:</th>
				<td>
					<h:selectOneMenu id="tipo" value="#{invencao.arquivo.tipo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItem itemValue="1" itemLabel="Artigo Científico"/>
						<f:selectItem itemValue="2" itemLabel="Resumo"/>
						<f:selectItem itemValue="3" itemLabel="Seminário"/>
						<f:selectItem itemValue="4" itemLabel="Conferência"/>
						<f:selectItem itemValue="5" itemLabel="Dissertação de Mestrado"/>
						<f:selectItem itemValue="6" itemLabel="Tese de Doutorado"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th  class="required">Título:</th>
				<td>
					<h:inputText  id="descricao" value="#{invencao.arquivo.descricao}" size="83" maxlength="90"/>
				</td>
			</tr>
			
			<tr>
				<th >Data:</th>
				<td>
					<t:inputCalendar 
						id="data" value="#{invencao.arquivo.data}" 
						renderAsPopup="true" renderPopupButtonAsImage="true"
						size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr>
				<th >Local:</th>
				<td>
					<h:inputText  id="local" value="#{invencao.arquivo.local}" size="50" maxlength="70"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<br/>
					<input type="hidden" value="2" id="categoriaArquivo" name="categoriaArquivo"/>
					<h:commandButton action="#{invencao.anexarArquivo}" value="Adicionar" id="btAnexarArqui" />
					<br/>
				</td>
			</tr>
			
			<c:if test="${not empty invencao.obj.arquivos}">
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Revelação
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario"> Lista de Revelações </td>
				</tr>
			
				<tr>
					<td colspan="2">
						<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
						<input type="hidden" value="0" id="idArquivoInvencao" name="idArquivoInvencao"/>
			
						<t:dataTable id="dataTableArquivos" value="#{invencao.obj.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
							<t:column  width="97%">
								<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
								<h:outputText value="#{anexo.descricao}" rendered="#{anexo.categoria == 2}"/>
							</t:column>
			
							<t:column>
								<h:commandButton image="/img/delete.gif" action="#{invencao.removeAnexo}"
									title="Remover Revelação"  alt="Remover Revelação" rendered="#{anexo.categoria == 2}"
									onclick="$(idArquivo).value=#{anexo.idArquivo};$(idArquivoInvencao).value=#{anexo.id};return confirm('Deseja Remover esta Revelação da Invenção?')" id="remArquivo" />
							</t:column>
			
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
			<tr>
				<td colspan="4">
					<h:panelGroup id="botoes">
						<h:commandButton value="<< Voltar" action="#{invencao.telaDadosGerais}" />
						<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" />
						<h:commandButton value="Avançar >>" action="#{invencao.submeterRevelacoes}" />
					</h:panelGroup>
				</td>
			</tr>
			</tfoot>
		</table>
		
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
