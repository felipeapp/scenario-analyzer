<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Alterar / Remover Fasc�culos Registrados</h2>

<div class="descricaoOperacao">
	<p> P�gina para altera��o dos dados ou remo��o dos fasc�culos que foram registrados. </p>
	<p> Apenas fasc�culos que ainda n�o tenham sido inclu�dos no acervo podem ser alterados nessa p�gina. 
	Se eles j� foram inclu�dos, s� podem ser alterados pelo setor de Processos T�cnicos.</p>
</div>



<f:view>


	<a4j:keepAlive beanName="registraChegadaFasciculoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>

	<h:form id="formRegistraChegadaPeriodico">
	
		<table class="formulario" style="width: 70%; margin-bottom: 20px;">
			
			<caption>Dados da Assinatura</caption>
			
			<tbody>
				
				<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada != null}">
					
					<tr>
						<th style="font-weight:bold; ">
							C�digo:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.codigo}</td>
					</tr>			
					<tr>
						<th style="font-weight:bold; ">
							T�tulo:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.titulo}</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Modalidade de Aquisi��o:
						</th>
						<td colspan="3" style="width: 50%">
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeCompra}">
								COMPRA
							</c:if>
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeDoacao}">
								DOA��O
							</c:if>
							<c:if test="${! registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeCompra &&  ! registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeDoacao  }">
								INDEFINIDO
							</c:if>
						</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Unidade de Destino:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.unidadeDestino.descricao}</td>
					</tr>
					
					<tr>
						<td colspan="6" style="height: 30px:">
						
						</td>
					</tr>
					
				</c:if>
				
			</tbody>
			
		</table>
		
		<div class="infoAltRem" style="margin-top: 10px">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Dados do Fasc�culo
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Apagar Fasc�culo
		</div>
		
		
		<%-- os fasc�culos  que j� foram registrados  --%>
		<table class="listagem" style="width: 100%">
			
			<caption>Fasc�culos Registrados para a Assinatura que ainda n�o foram inclu�dos no acervo ( ${registraChegadaFasciculoMBean.qtdFasciculosRegistradosDaAssinatura } )</caption>
			
			<thead>
				<tr>
					<th style="text-align: left;">C�digo Barras</th>
					<th style="text-align: center;">Ano Cron.</th>
					<th>Dia/M�s</th>
					<th>Ano</th>
					<th>N�mero</th>
					<th>Volume</th>
					<th>Edi��o</th>
					<th>Usu�rio que realizou o registro</th>
					<th style="text-align: center">Data/Hora de Registro</th>
					<th style="text-align: center; width: 1%"> </th>
					<th style="text-align: center; width: 1%"> </th>
				</tr>
			</thead>
				
			<c:forEach var="fasciculo" items="#{registraChegadaFasciculoMBean.fasciculosRegistradosDaAssinatura}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					
					<td>${fasciculo.codigoBarras}</td>
					
					<td style="text-align: center;">${fasciculo.anoCronologico}</td>
					
					<td>${fasciculo.diaMes}</td>
					
					<td>${fasciculo.ano}</td>
					
					<td>${fasciculo.numero}</td>
					
					<td>${fasciculo.volume}</td>
					
					<td>${fasciculo.edicao}</td>
					
					<td>${fasciculo.registroCriacao.usuario.nome}</td>
					
					<td style="text-align: center;">  <ufrn:format type="dataHora" valor="${fasciculo.dataCriacao}"> </ufrn:format> </td>
					
					
					<td>
						<h:commandLink action="#{registraChegadaFasciculoMBean.preparaAlteracaoDadosFasciculos}">
							<h:graphicImage url="/img/alterar.gif" style="border:none" title="Alterar Dados do Fasc�culo" />
							<f:param name="idFasciculoAlteracao" value="#{fasciculo.id}"></f:param> 
						</h:commandLink>
					</td>	
					
					<td>
						<h:commandLink action="#{registraChegadaFasciculoMBean.removeFasciculoRegistrado}"  onclick="return confirm('Tem certeza que deseja remover este fasc�culo ? ');">
							<h:graphicImage url="/img/delete.gif" style="border:none" title="Apagar Fasc�culo" />
							<f:param name="idFasciculoRemocao" value="#{fasciculo.id}"></f:param> 
						</h:commandLink>
					</td>
					
				</tr>
				
			</c:forEach>
					
			<tfoot>
				<tr style="text-align: center">
					<td colspan="11">
						<h:commandButton value="Cancelar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
			
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>