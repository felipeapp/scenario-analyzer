<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

<h2> <ufrn:subSistema />  > Configurar Relacionamento com �reas do CNPq </h2>


<div class="descricaoOperacao">
   <p>Abaixo � mostrada a rela��o entre as grandes �reas de conhecimento do CNPq, e as classifica��es bibliogr�ficas utilizadas no sistema de biblioteca.
   O sistema utiliza essa rela��o para descobrir a �rea CNPq dos T�tulos no acervo a partir da classifica��o que lhe foi dada.</p>
   <br/>
   <p> O relacionamento entre as  �reas do CNPq e as classifica��es bibliogr�ficas n�o � simples e direto, por isso foram criadas algumas conve��es e mecamismos de como preencher essas informa��es no sistema.</p>
    <br/>
   
  	<table style="border: 1px solid;">
  		<caption style="font-weight: bold; font-variant: small-caps;"> Dicas de Preenchimento do Formul�rio </caption>
  		
  		<tr>
  			<td> 
  			<ul>
  			 	<li>Separe os c�digos das classifica��es bibliogr�fica utilizando espa�o em branco. </li>
  			 	<br/>
  			 	<li> Para intervalos de classes utilize a nota��o de tra�o.   Por exemplo <strong><i>600-608</i></strong> engloba todas as classe que come�am com <strong><i>classe 600 at� a classe 608</i></strong>.
  				  Informe intervalos de mesmo tamanho, por exemplo: <strong><i>6 a 608</i></strong>, tem que ser informado o intervalo: <strong><i>600-608</i></strong>  </li>
  			 	<br/>
  			 	<li>As classes de exclus�o s�o utilizadas para mapear relacionamentos complexos, por exemplo, todo T�tulo que come�ar com a classe "1" pertence a �rea do CNPq X, exeto se come�ar com a classe "123" porque pertence a �rea do CNPq Y. </li>
  			 </ul>	  
  			</td>
  		</tr>
  		
  	</table>
   
   
</div>

<a4j:keepAlive beanName="relacionamentoClassificacaoBibliograficaAreasCNPqMBean" />

<h:form id="formAlteraAssociacaoCNPqClassificacao">


	<table class="formulario" style="width: 95%">
		<caption> Rela��o entre Grandes �reas CNPq e Classifica��es Bibliogr�ficas</caption>
		
		<%-- Parte na qual o usu�rio escolhe qual a classifica��o bibliogr�fica deseja relacionar com a �rea CNPq  --%>
		<tr>
			<td colspan="2">
				<table style="width: 80%; margin-left: auto; margin-right: auto;">
					<tr>
						<th style="text-align: right; width: 60%;"> Classifica��o Bibliogr�fica: </th>
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
						Classes ${relacionamentoClassificacaoBibliograficaAreasCNPqMBean.classificacoEscolhida.descricao} Inclus�o:
					</th>
					<td>
						<h:inputText value="#{relacionamento.classesInclusao}" size="120" maxlength="200" />
						<ufrn:help> As classes ou faixa de classes da classifica��o escolhida que pertencem a essa �rea do CNPq. </ufrn:help>
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaImpar" : "linhaPar"}">	
					<th>
						Classes ${relacionamentoClassificacaoBibliograficaAreasCNPqMBean.classificacoEscolhida.descricao} Exclus�o:
					</th>
					<td>
						<h:inputText value="#{relacionamento.classesExclusao}" size="120" maxlength="200" />
						<ufrn:help> As classes ou faixa de classes da classifica��o escolhida que excluem o T�tulo de pertencer a essa �rea, mesmo possuindo uma das classes de inclus�o. </ufrn:help>    
					</td>
				</tr>
			</c:forEach>
		</c:if>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cmdAtualizaRelacaoCNPqClassificacao" value="Atualizar Relacionamento" action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.atualizarRelacionamento}"  onclick="return confirm('Confirma a atualiza��o dos relacionamentos no sistema ? ');" disabled="#{! relacionamentoClassificacaoBibliograficaAreasCNPqMBean.botaoAtualizarHabilitado}" />
					<h:commandButton id="cmdCancelaAtualizacaoRelacaoCNPqClassificacao" value="Cancelar" action="#{relacionamentoClassificacaoBibliograficaAreasCNPqMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
		
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>