<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Notifica��o de Inven��o &gt; Revela��es</h2>

	<h:form id="form">
		
		<table class="formulario" width="90%">
			<caption>Revela��es</caption>
			
			<tr>
				<td colspan="2" class="subFormulario"> Revela��o da Inven��o </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Caso j� tenha sido realizada ou se est� para haver revela��o referente ao objeto da inven��o,
							informar o tipo de publica��o, a data da publica��o (ou data prov�vel) e o local de publica��o.
						</p>
					</div> 
				</td>
			</tr>
			
			<tr>
				<th >Tipo de Publica��o:</th>
				<td>
					<h:selectOneMenu id="tipo" value="#{invencao.arquivo.tipo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItem itemValue="1" itemLabel="Artigo Cient�fico"/>
						<f:selectItem itemValue="2" itemLabel="Resumo"/>
						<f:selectItem itemValue="3" itemLabel="Semin�rio"/>
						<f:selectItem itemValue="4" itemLabel="Confer�ncia"/>
						<f:selectItem itemValue="5" itemLabel="Disserta��o de Mestrado"/>
						<f:selectItem itemValue="6" itemLabel="Tese de Doutorado"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th  class="required">T�tulo:</th>
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
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �">
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
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Revela��o
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario"> Lista de Revela��es </td>
				</tr>
			
				<tr>
					<td colspan="2">
						<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
						<input type="hidden" value="0" id="idArquivoInvencao" name="idArquivoInvencao"/>
			
						<t:dataTable id="dataTableArquivos" value="#{invencao.obj.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
							<t:column  width="97%">
								<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
								<h:outputText value="#{anexo.descricao}" rendered="#{anexo.categoria == 2}"/>
							</t:column>
			
							<t:column>
								<h:commandButton image="/img/delete.gif" action="#{invencao.removeAnexo}"
									title="Remover Revela��o"  alt="Remover Revela��o" rendered="#{anexo.categoria == 2}"
									onclick="$(idArquivo).value=#{anexo.idArquivo};$(idArquivoInvencao).value=#{anexo.id};return confirm('Deseja Remover esta Revela��o da Inven��o?')" id="remArquivo" />
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
						<h:commandButton value="Avan�ar >>" action="#{invencao.submeterRevelacoes}" />
					</h:panelGroup>
				</td>
			</tr>
			</tfoot>
		</table>
		
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
