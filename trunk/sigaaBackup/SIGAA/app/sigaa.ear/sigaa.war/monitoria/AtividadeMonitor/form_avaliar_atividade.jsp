<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	
	<script type="text/javascript">
	
								function validar(obj){
							
										freq = $('freq');
										obs = $('obs');								
		
										if(obj.value == 'true'){
										   freq.show();
										   obs.show();								   
										}else{
											freq.hide();										
											obs.hide();																			
										}
								
								}
	</script>
	

	<h2><ufrn:subSistema /> > Avaliando Relatório de Atividades do Monitor</h2>
	<br>
<h:form id="formOrientadorValidaAtividade">

	<h:messages/>

	<table class="formulario" width="100%">
	<caption class="listagem">Relatório de Atividades do Monitor</caption>

	<tr>
		<th width="25%"><b>Projeto:</b></th> 
		<td> 		
			<h:outputText value="#{ atividadeMonitor.obj.discenteMonitoria.projetoEnsino.anoTitulo }" />
		</td>
	</tr>


	<tr>
		<th><b>Monitor:</b></th> 
		<td> 		
			<h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.discente.matriculaNome}"/>
		</td>
	</tr>


	<tr>
		<th><b>Mês de Referência:</b></th> 
		<td> 		
			<h:outputText value="#{ atividadeMonitor.obj.mes }"/>/<h:outputText value="#{ atividadeMonitor.obj.ano }"/>
		</td>
	</tr>

	<tr>
		<th><b>Data de Cadastro:</b></th> 
		<td> 		
			<fmt:formatDate value="${ atividadeMonitor.obj.dataCadastro }" pattern="dd/MM/yyyy HH:mm:ss"/>
		</td>
	</tr>

	<tr>
		<th><b>Atividades Desenvolvidas:</b></th> 
		<td> 		
		 <h:outputText id="txtAtividades" value="#{ atividadeMonitor.obj.atividades }"/> 
		</td>
	</tr>
	
	<tr>
		<td  colspan="2">
			<table width="100%">
				<tr>											
					<td><caption class="listagem"> Avalição das Atividades do Monitor </caption><br/></td>
				</tr>
				
			
				<tr>
					<th width="25%">Validação das Atividades:</th> 
					<td> 	
						<h:selectOneMenu
								value="#{ atividadeMonitor.obj.validadoOrientador }" id="selectValidar" onchange="javascript:validar(this)">
								<f:selectItem itemLabel="VALIDAR RELATÓRIO" itemValue="true" />
								<f:selectItem itemLabel="NÃO VALIDAR RELATÓRIO" itemValue="false" />
						</h:selectOneMenu>						 
					</td>
				</tr>
			
			
				<tr id="freq" style="display: ${ atividadeMonitor.obj.validadoOrientador ? '':'none' }">
					<th>Freqüência:</th> 
					<td> 		
						<h:selectOneMenu
								value="#{ atividadeMonitor.obj.frequencia }" id="txtFrequencia">
								<f:selectItem itemLabel="0%" itemValue="0"/>								
								<f:selectItem itemLabel="100%" itemValue="100"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Nova resolução limitou a validação de freqüências para 0% ou 100%</ufrn:help>						
					</td>
				</tr>
			
				<tr id="obs" style="display: ${ atividadeMonitor.obj.validadoOrientador ? '':'none' }">
					<td colspan="2">Observações:<br/>
					 <h:inputTextarea id="txaObservacoesOrientador"  value="#{ atividadeMonitor.obj.observacaoOrientador }" rows="5" style="width:98%"/> </td>
				</tr>
				
			</table>
		</td>	
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{atividadeMonitor.confirmButton}" action="#{atividadeMonitor.orientadorValidarAtividade}"/>
					<h:commandButton value="Cancelar" action="#{atividadeMonitor.cancelar}"/>
			</td>
		</tr>
	</tfoot>
</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>