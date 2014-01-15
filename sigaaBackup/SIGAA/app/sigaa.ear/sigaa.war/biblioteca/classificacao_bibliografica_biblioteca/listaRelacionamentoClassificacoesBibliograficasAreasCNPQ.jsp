<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

<h2> <ufrn:subSistema />  > Configurar Relacionamento com Áreas do CNPq </h2>


<div class="descricaoOperacao">
   <p>Abaixo é mostrada a relação entre as grandes Áreas de conhecimento do CNPq, e as classificações bibliográficas utilizadas no sistema de biblioteca.
   O sistema utiliza essa relação para descobrir a área CNPq dos Títulos no acervo a partir da classificação que lhe foi dada.</p>
   <br/>
   <p> O relacionamento entre as  áreas do CNPq e as classificações bibliográficas não é simples e direto, por isso foram criadas algumas conveções e mecamismos de como preencher essas informações no sistema.</p>
    <br/>
   
  	<table style="border: 1px solid;">
  		<caption style="font-weight: bold; font-variant: small-caps;"> Dicas de Preenchimento do Formulário </caption>
  		
  		<tr>
  			<td> 
  			<ul>
  			 	<li>Separe os códigos das classificações bibliográfica utilizando espaço em branco. </li>
  			 	<br/>
  			 	<li> Para intervalos de classes utilize a notação de traço.   Por exemplo <strong><i>600-608</i></strong> engloba todas as classe que começam com <strong><i>classe 600 até a classe 608</i></strong>.
  				  Informe intervalos de mesmo tamanho, por exemplo: <strong><i>6 a 608</i></strong>, tem que ser informado o intervalo: <strong><i>600-608</i></strong>  </li>
  			 	<br/>
  			 	<li>As classes de exclusão são utilizadas para mapear relacionamentos complexos, por exemplo, todo Título que começar com a classe "1" pertence a área do CNPq X, exeto se começar com a classe "123" porque pertence a área do CNPq Y. </li>
  			 </ul>	  
  			</td>
  		</tr>
  		
  	</table>
   
   
</div>

<a4j:keepAlive beanName="relacionamentoClassificacaoBibliograficaAreasCNPqMBean" />

<h:form id="formAlteraAssociacaoCNPqClassificacao">


	<table class="formulario" style="width: 95%">
		<caption> Relação entre Grandes Áreas CNPq e Classificações Bibliográficas</caption>
		
		<%-- Parte na qual o usuário escolhe qual a classificação bibliográfica deseja relacionar com a área CNPq  --%>
		<tr>
			<td colspan="2">
				<table style="width: 80%; margin-left: auto; margin-right: auto;">
					<tr>
						<th style="text-align: right; width: 60%;"> Classificação Bibliográfica: </th>
						<td> 
							<h:selectOneMenu id="comboTipoAutoridade" value="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.idClassificacaoEscolhida}">
								<f:selectItem itemLabel="--Selecione--" itemValue="-1" />
								<f:selectItems value="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.allClassificacoesCombo}" />
								<a4j:support event="onchange" reRender="formAlteraAssociacaoCNPqClassificacao" actionListener="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.alterouClassificacao}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</table>
				
			</td>
		</tr>
		
		
		<c:if test="${relacionamentoClassificacaoBibliograficaAreasCNPqMBean.relacionamentosSalvos != null && relacionamentoClassificacaoBibliograficaAreasCNPqMBean.idClassificacaoEscolhida > 0}">
			<c:forEach items="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.relacionamentosSalvos}" var="relacionamento" varStatus="status">
				<tr style="background: #DEDFE3; height: 30px;">
					<th colspan="2" style="font-weight: bold; text-align: left;">
						${relacionamento.area.sigla} - ${relacionamento.area.nome} 
					</th>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
					<th>
						Classes ${relacionamentoClassificacaoBibliograficaAreasCNPqMBean.classificacoEscolhida.descricao} Inclusão:
					</th>
					<td>
						<h:inputText value="#{relacionamento.classesInclusao}" size="120" maxlength="200" />
						<ufrn:help> As classes ou faixa de classes da classificação escolhida que pertencem a essa área do CNPq. </ufrn:help>
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaImpar" : "linhaPar"}">	
					<th>
						Classes ${relacionamentoClassificacaoBibliograficaAreasCNPqMBean.classificacoEscolhida.descricao} Exclusão:
					</th>
					<td>
						<h:inputText value="#{relacionamento.classesExclusao}" size="120" maxlength="200" />
						<ufrn:help> As classes ou faixa de classes da classificação escolhida que excluem o Título de pertencer a essa área, mesmo possuindo uma das classes de inclusão. </ufrn:help>    
					</td>
				</tr>
			</c:forEach>
		</c:if>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cmdAtualizaRelacaoCNPqClassificacao" value="Atualizar Relacionamento" action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.atualizarRelacionamento}"  onclick="return confirm('Confirma a atualização dos relacionamentos no sistema ? ');" disabled="#{! relacionamentoClassificacaoBibliograficaAreasCNPqMBean.botaoAtualizarHabilitado}" />
					<h:commandButton id="cmdCancelaAtualizacaoRelacaoCNPqClassificacao" value="Cancelar" action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
		
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>