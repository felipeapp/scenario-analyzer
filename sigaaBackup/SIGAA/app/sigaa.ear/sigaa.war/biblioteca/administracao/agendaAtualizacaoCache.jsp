<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2> <ufrn:subSistema />  > Agenda Atualização do Cache dos dados MARC dos Títulos </h2>


<div class="descricaoOperacao">
   <p>
  		<strong>Importante: </strong>  Sempre que for adicionado ou retirado um campo do cache, realizar as alterações primeiro o códico da classe  
  		<i> AtualizaCacheMARCTitulosThread </i> antes de executar a atualização.
   </p>
   
   <p> Recomenda-se agendar a atualização para um horário em que o sistema não esteja sendo usado. Caso queira que a atualização ocorra imediatamente, 
   informe uma hora menor que hora atual.</p>
   
</div>

<f:view>

	<a4j:keepAlive beanName="atualizaCacheMARCTitulosMBean" />

	<h:form id="formAlteraAssociacaoCNPqClassificacao">
	
		<table class="formulario" style="width: 90%">
			
			<caption> Dados da atualização </caption>
		
			
			<tr class="linhaImpar">
				<td colspan="6" style="text-align: left; padding-left: 10%">
					
						<h:selectOneRadio id="campoAtualizacao" layout="pageDirection" value="#{atualizaCacheMARCTitulosMBean.posicaoCampoSelecionado}">
							<c:forEach var="campo" items="#{atualizaCacheMARCTitulosMBean.camposCache}" varStatus="status">
								 <f:selectItem itemLabel="#{campo.descricao}" itemValue="#{campo.posicao}" />  
							</c:forEach>
						</h:selectOneRadio>
					
					<br/><br/>
				</td>
			</tr>
			
		
			<tr>
				<th class="required" colspan="4">Hora para agendamento da atualização:</th> 
				<td colspan="2"> 
					<h:inputText value="#{atualizaCacheMARCTitulosMBean.horaExecucao}" size="5" maxlength="5" onkeypress="return (formataHora(this, event));" id="horarioExecucao" >
						<f:converter converterId="convertHora"/>
					</h:inputText>
				</td>
			</tr>
		
			<tr>
				<th class="required" colspan="4">Email para receber confirmação da atualização:</th> 
				<td colspan="2"> <h:inputText size="50" maxlength="100"  value="#{atualizaCacheMARCTitulosMBean.email}" /></td>
			</tr>
		
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton id="cmdAtualizaCacheTitulos" value="Atualizar Relacionamento" action="#{atualizaCacheMARCTitulosMBean.atualizarCacheTitulos}"  onclick="return confirm('Confirma a atualização do cache de Títulos do sistema ? ');" />
						<h:commandButton id="cmdCancelaAtualizacaoCacheTitulos" value="Cancelar" action="#{atualizaCacheMARCTitulosMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		
		</table>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>