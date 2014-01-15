<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Prestação de Serviços</h2>
<br>

<h:form> 

	<table class=formulario width="95%">
		<caption class="listagem">Caracterização do Serviço Prestado</caption>
	

		<tr>
			<td>
				<b> Fator Gerador:</b><br>
				<h:selectOneMenu id="fatorGerador" value="#{prestacaoServico.obj.fatorGerador.id}" readonly="#{prestacaoServico.readOnly}">
				  <f:selectItems  value="#{prestacaoServico.allFatorGeradorCombo}" />
				</h:selectOneMenu> 						
			</td>
			
			<td>
				<b> Forma de Compromisso:</b><br>
				<h:selectOneMenu id="formaCompromisso" value="#{prestacaoServico.obj.formaCompromisso.id}" readonly="#{prestacaoServico.readOnly}">
				  <f:selectItems  value="#{prestacaoServico.allFormaCompromissoCombo}" />
				</h:selectOneMenu> 						
			</td>
		</tr>
		

		<tr>
			<td>
				<b> Natureza do Serviço:</b><br>
				<h:selectOneMenu id="naturezaServico" value="#{prestacaoServico.obj.naturezaServico.id}" readonly="#{prestacaoServico.readOnly}">
				  <f:selectItems  value="#{prestacaoServico.allNaturezaServicoCombo}" />
				</h:selectOneMenu> 						
			</td>
			
			<td>
				<b>Período de Desenvolvimento:</b><br/>
				Inicio:<t:inputCalendar value="#{prestacaoServico.obj.dataInicio}" renderAsPopup="true" renderPopupButtonAsImage="true"	size="12"/>
				Fim:<t:inputCalendar value="#{prestacaoServico.obj.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true" size="12"/>
				<span class="required"></span>
			</td>
			
		</tr>
		
		
		<tr>
			<td colspan="2">
				<B> Instituições Envolvidas:</B><br>
				<h:inputTextarea id="instituicoesEnvolvidas" value="#{prestacaoServico.obj.instituicoesEnvolvidas}" rows="2" cols="110" readonly="#{prestacaoServico.readOnly}"/>
			</td>
		</tr>


		<tfoot>
			<tr> 
				<td colspan=2>
					<h:commandButton value="<< Voltar" action="#{prestacaoServico.irTelaEquipe}" />				
					<h:commandButton value="Cancelar" action="#{prestacaoServico.cancelar}" />
					<h:commandButton value="Avançar >>" action="#{prestacaoServico.irTelaDescricao}" />
				</td> 
			</tr>
		</tfoot>


	</table>
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>