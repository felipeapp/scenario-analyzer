<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema/> &gt; Histórico de Interrupções Cadastradas no Sistema </h2>
	
	
	<h:form>
	
		<table class="formulario" width="70%">
			<caption class="formulario">Filtro da Busca</caption>
			
			<tbody>			
				
				<tr>
					<th style="text-align: center">Biblioteca:</th>
					<td>
						<h:selectOneMenu id="comboboxBibliotecasHistorico" value="#{interrupcaoBibliotecaMBean.idBibliotecaHistorico}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{bibliotecaMBean.allCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="obrigatorio">Período:</th>
					<td>
						<table>
							<tr>
								<td>
									<t:inputCalendar id="Inicio" value="#{interrupcaoBibliotecaMBean.dataInicio}" renderAsPopup="true"
											popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)"
											renderPopupButtonAsImage="true" size="10" maxlength="10" />
								</td>
								<td>a</td>
								<td>
									<t:inputCalendar id="Fim" value="#{interrupcaoBibliotecaMBean.dataFim}" renderAsPopup="true"
											popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)"
											renderPopupButtonAsImage="true" size="10" maxlength="10" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
		
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Visualizar" action="#{interrupcaoBibliotecaMBean.visualizarHistorico}" />
						<h:commandButton value="Cancelar" action="#{interrupcaoBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
			
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>