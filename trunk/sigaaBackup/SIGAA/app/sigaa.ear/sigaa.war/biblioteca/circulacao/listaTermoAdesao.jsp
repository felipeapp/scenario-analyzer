<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive  beanName="emiteTermoAdesaoBibliotecaMBean" />

	<a4j:keepAlive  beanName="buscaUsuarioBibliotecaMBean" />

	<h2> <ufrn:subSistema/> >  Emitir Termo de Ades�o </h2>

	<h:form id="formListaTermoAdesao">
	
			<div class="descricaoOperacao" style="width: 70%;">
				Abaixo est�o listados os termos de ades�o assinados pelo usu�rio para usar o sistema de bibliotecas. 
				� poss�vel selecionar e reimprimir qualquer termo assinado por ele.
			</div>
		
			<div class="infoAltRem" style="width: 80%; margin-top: 10px">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Visualizar Termo de Ades�o do Usu�rio Selecionado
			</div>
		
			<table class="listagem" style="width: 80%;">
				<caption> Termos de Ades�o( ${emiteTermoAdesaoBibliotecaMBean.qtdTermosDeAdesaoAssinados} ) </caption>
				<thead>
					<tr align="center">
						<th style="width: 20%;"> CPF/Passaporte </th>
						<th style="width: 59%;"> Nome </th>
						<th style="width: 20%;"> Data da Assinatura </th>
						<th style="width: 1%;"> </th>
					</tr>
				</thead>
				<tbody>	
				
					<c:if test="${emiteTermoAdesaoBibliotecaMBean.qtdTermosDeAdesaoAssinados == 0  }">
						<tr>
							<td colspan="4" style="text-align: center; color: red;">Usu�rio selecionado n�o assinou termo de ades�o � biblioteca.</td>
						</tr>
					</c:if>
				
					<c:if test="${emiteTermoAdesaoBibliotecaMBean.qtdTermosDeAdesaoAssinados > 0  }">
						<c:forEach items="#{emiteTermoAdesaoBibliotecaMBean.termosDeAdesaoAssinados}" var="termo" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>${termo.cpfPassaporte}</td>
								<td>${termo.nomePessoa}</td>
								<td>${termo.dataFormatada}</td>
								<td width="1%" style="text-align:center">
									<h:commandLink action="#{emiteTermoAdesaoBibliotecaMBean.imprimirTermoImpressao}" target="_blank"> 
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Visualizar Termo" />
										<f:param name="idTermoAdesao" value="#{termo.id}"/>
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</c:if>	
						
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="4" style="text-align: center;">
							<h:commandButton id="cancelarListaTermo" value="Cancelar"  action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}" immediate="true"/>	
						</td>
					</tr>
				</tfoot>
				
			</table>
		
	</h:form>

</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
