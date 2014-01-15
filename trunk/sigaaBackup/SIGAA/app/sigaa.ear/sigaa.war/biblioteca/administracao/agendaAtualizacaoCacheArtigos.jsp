<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2> <ufrn:subSistema />  > Agenda Atualiza��o do Cache dos dados MARC dos T�tulos </h2>


<div class="descricaoOperacao">
   <p>
  		<strong>Importante: </strong>  Sempre que for adicionado ou retirado um campo do cache de artigos, realizar as altera��es primeiro o c�dico da classe  
  		<i> AtualizaCacheMARCArtigosThread </i> antes de executar a atualiza��o.
   </p>
   
   <p> Recomenda-se agendar a atualiza��o para um hor�rio em que o sistema n�o esteja sendo usado. Caso queira que a atualiza��o ocorra imediatamente, 
   informe uma hora menor que hora atual.</p>
   
</div>

<f:view>

	<a4j:keepAlive beanName="atualizaCacheMARCArtigosMBean" />

	<h:form id="formAlteraAssociacaoCNPqClassificacao">
	
		<table class="formulario" style="width: 90%">
			
			<caption> Dados da atualiza��o </caption>
		
			
			<tr class="linhaImpar">
				<td colspan="6" style="text-align: left; padding-left: 10%">
					
					<h:selectOneRadio id="campoAtualizacao" layout="pageDirection" value="#{atualizaCacheMARCArtigosMBean.posicaoCampoSelecionado}">
						<c:forEach var="campo" items="#{atualizaCacheMARCArtigosMBean.camposCache}" varStatus="status">
							 <f:selectItem itemLabel="#{campo.descricao}" itemValue="#{campo.posicao}" />  
						</c:forEach>
					</h:selectOneRadio>
					
				</td>
			</tr>
			
		
			<tr>
				<th class="required" colspan="4">Hora para agendamento da atualiza��o:</th> 
				<td colspan="2"> 
					<h:inputText value="#{atualizaCacheMARCArtigosMBean.horaExecucao}" size="5" maxlength="5" onkeypress="return (formataHora(this, event));" id="horarioExecucao" >
						<f:converter converterId="convertHora"/>
					</h:inputText>
				</td>
			</tr>
		
			<tr>
				<th class="required" colspan="4">Email para receber confirma��o da atualiza��o:</th> 
				<td colspan="2"> <h:inputText size="50" maxlength="100"  value="#{atualizaCacheMARCArtigosMBean.email}" /></td>
			</tr>
		
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton id="cmdAtualizaCacheTitulos" value="Atualizar Relacionamento" action="#{atualizaCacheMARCArtigosMBean.atualizarCacheArtigos}"  onclick="return confirm('Confirma a atualiza��o do cache de Artigos do sistema ? ');" />
						<h:commandButton id="cmdCancelaAtualizacaoCacheTitulos" value="Cancelar" action="#{atualizaCacheMARCArtigosMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		
		</table>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>