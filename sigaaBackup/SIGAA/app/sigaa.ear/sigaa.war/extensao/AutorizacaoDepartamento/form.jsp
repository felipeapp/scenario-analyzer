<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Autoriza��o da A��o Acad�mica</h2>

		<h:form id="form">

			<table class="formulario" width="95%">
				<caption class="listagem">An�lise da Proposta de A��o Acad�mica</caption>

			<tr>
				<td colspan="2">
				
					<table class="subFormulario" width="100%">
					<caption>Detalhes da A��o Acad�mica</caption>

						<tr>
							<th width="21%">T�tulo:</th>
							<td ><b><h:outputText value="#{autorizacaoDepartamento.obj.atividade.anoTitulo}" id="titulo"/></b>
							</td>
						</tr>

						<tr>
							<th width="21%">Tipo A��o:</th>
							<td><b><h:outputText value="#{autorizacaoDepartamento.obj.atividade.tipoAtividadeExtensao.descricao}" id="tipo_acao"/></b>
							</td>
						</tr>
						
						<tr>
							<th width="21%">Fonte de Financiamento:</th>
							<td><b><h:outputText value="#{autorizacaoDepartamento.obj.atividade.fonteFinanciamentoString}" id="fonte_financiamento"/></b></td>
						</tr>
						
						<tr>
							<th width="21%"> Tipo de Cadastro: </th>
							<td><b> <h:outputText value="#{autorizacaoDepartamento.obj.atividade.registro ? 'SOLICITA��O DE REGISTRO' : 'SUBMISS�O DE PROPOSTA'}" id="tipo_cadastro"/></b> </td>
						</tr>
						
						<tr>
							<th width="21%">�rea Tem�tica Principal:</th>
							<td><b><h:outputText value="#{autorizacaoDepartamento.obj.atividade.areaTematicaPrincipal.descricao}" id="area_tematica"/></td>
						</tr>

						<tr>
							<th width="21%">�rea CNPq:</th>
							<td><b><h:outputText value="#{autorizacaoDepartamento.obj.atividade.areaConhecimentoCnpq.nome}" id="area_cnpq"/></b></td>
						</tr>


						<tr>
							<td colspan="2">				
									<t:dataTable value="#{autorizacaoDepartamento.obj.atividade.membrosEquipe}" var="membro" align="center" width="100%" id="ori_">											
												<f:facet name="header">
													<f:verbatim><b>Envolvidos na A��o de Extens�o</b></f:verbatim>
												</f:facet>

												<t:column>
													<h:outputText value="<b>"  rendered="#{(not empty membro.servidor) && (membro.servidor.unidade.id == sessionScope.usuario.unidade.id)}" escape="false" id="serv_"/>
														<h:outputText value="#{membro.pessoa.nome} (#{membro.servidor.unidade.sigla})"  rendered="#{not empty membro.servidor}" id="serv1_"/>													
													<h:outputText value="</b>"  rendered="#{(not empty membro.servidor) && (membro.servidor.unidade.id == sessionScope.usuario.unidade.id)}" escape="false" id="serv2_"/>
													
													<h:outputText value="#{membro.pessoa.nome}"  rendered="#{empty membro.servidor}" id="serv4_"/>
												</t:column>

												<t:column>
													<h:outputText value="<b>"  rendered="#{(not empty membro.servidor) && (membro.servidor.unidade.id == sessionScope.usuario.unidade.id)}" escape="false"/>
														<h:outputText value="#{membro.funcaoMembro.descricao}"/>													
													<h:outputText value="</b>"  rendered="#{(not empty membro.servidor) && (membro.servidor.unidade.id == sessionScope.usuario.unidade.id)}" escape="false"/>
												</t:column>												
																
									</t:dataTable>
							</td>
						  </tr>
						</table>
							
				</td>
			</tr>

			<tr>
				<td>
					<hr/>																
					<table class="subFormulario" width="100%">
							<caption>Parecer</caption>
					
								<tr>
									<th  width="30%" class="required"><b>Parecer:</b></th>
									<td>	
										<h:selectOneMenu id="autorizadoDpto" value="#{autorizacaoDepartamento.obj.autorizado}" onchange="javascript:validar(this)">
											<f:selectItem itemValue="" itemLabel="-- SELECIONE UMA OP��O --"/>
											<f:selectItem itemValue="TRUE" itemLabel="VALIDAR"/>
											<f:selectItem itemValue="FALSE" itemLabel="N�O VALIDAR"/>						
										</h:selectOneMenu>
										<br/>
									</td>
								</tr>
					
								<tr id="tipoAtorizacao" style="display: ${ autorizacaoDepartamento.obj.autorizado ? '':'none' }">
									<th class="required"><b>Tipo de Autoriza��o:</b> </th>
									<td>
										<h:selectOneMenu value="#{autorizacaoDepartamento.obj.tipoAutorizacao.id}" id="tipo_autorizacao">
												<f:selectItem itemLabel="-- SELECIONE UM TIPO DE AUTORIZA��O --" itemValue="0" />
												<f:selectItems value="#{autorizacaoDepartamento.tiposAutorizacoesCombo}"/>
										</h:selectOneMenu>
									</td>
								</tr>
					
								<tr id="dataReuniao" style="display: ${ autorizacaoDepartamento.obj.autorizado ? '':'none' }">
									<th class="required"><b> Data da Reuni�o:</b> </th>
									<td>
										<t:inputCalendar id="data_reuniao" value="#{autorizacaoDepartamento.obj.dataReuniao}" 
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" 
										onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupDateFormat="dd/MM/yyyy" />
									</td>
								</tr>
					
								<tfoot>
									<tr>
										<td colspan="2">
											<input type="hidden" value="false" name="relatorio" id="relatorio"/>
											<h:commandButton value="#{autorizacaoDepartamento.confirmButton}" action="#{autorizacaoDepartamento.autorizar}" id="confirmar"/> 
											<h:commandButton value="Cancelar" action="#{autorizacaoDepartamento.cancelar}" id="cancelar" onclick="#{confirm}" />
										</td>
									</tr>
								</tfoot>
						</table>
						
				</td>
			</tr>
			
	</table>
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center><br/>
	</h:form>

</f:view>

	<script type="text/javascript">
		function validar(obj){
				tipo = $('tipoAtorizacao');
				data = $('dataReuniao');								

				if(obj.value == 'TRUE' || obj.value == ''){
				   tipo.show();
				   data.show();			   
				}else{
					tipo.hide();
					data.hide();			   
				}
		}
	</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>