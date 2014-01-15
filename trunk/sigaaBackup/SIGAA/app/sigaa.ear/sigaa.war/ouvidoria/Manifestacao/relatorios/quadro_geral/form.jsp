<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="relatoriosOuvidoria" />

<f:view>
<h2><ufrn:subSistema/> > Quadro Geral de Manifesta��es</h2>

	<div class="descricaoOperacao">
		<p>
			Caro Usu�rio,
		</p> 	
		<p>
			Para gerar o quadro geral de manifesta��es, voc� deve fornecer um per�odo de cadastro da manifesta��o.
		</p> 	
	</div>

	<h:form id="formulario">
		<table class="formulario" style="width:58%;">
			<caption> Informe os crit�rios de busca</caption>
			<tbody>
				<tr>
					<td width="2%"></td>
					<th style="text-align: left" width="150px">
						<h:outputText styleClass="required">Per�odo de Cadastro:</h:outputText>
					</th>
					<td> 
						De: <t:inputCalendar value="#{relatoriosOuvidoria.dataInicial }" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �"
									id="Data_Inicial" size="10" maxlength="10" onkeypress="return formataData(this,event)" />
						a: <t:inputCalendar value="#{relatoriosOuvidoria.dataFinal }" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �"
									id="Data_Final" size="10" maxlength="10" onkeypress="return formataData(this,event)" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{relatoriosOuvidoria.montarQuadroGeral }" value="Gerar Relat�rio" id="buscar" />
						<h:commandButton action="#{relatoriosOuvidoria.cancelar }" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>