<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style>
	.desatividada{
		color: grey;
		font-style: italic;
	}
	
	.destaquevermelho{
		color: red; 
		font-weight:bold;
	}
	
</style>

<f:view>
	<h2><ufrn:subSistema /> &gt; Lista de Inscrições de ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividades' : 'Mini Atividade'}</h2>
	
	<div class="descricaoOperacao">
		<p>Caro (a) Usuário (a),</p>
		<p>Abaixo estão listadas as inscrições existentes para a
			
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				 Atividade: <strong> ${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.titulo} </strong> 
			</c:if>
			
			<c:if test="${! gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				 Mini Atividade: <strong> ${gerenciarInscricoesCursosEventosExtensaoMBean.subAtividadeSelecionada.titulo} </strong> 
			</c:if>
		</p>
		<br />
		<p> Por meio dessa operação é possível criar novos períodos de inscrição, alterar os dados de um período de inscrição, suspender alguma inscrição, entre outras. </p>
		<p> Os usuários devem ser orientados a acessarem a área pública do sistema para realizar a inscrição. </p>
		<br/>
		<p>  <strong>IMPORTANTE:</strong> É possível abrir mais de um período de inscrição, desde que os períodos não sejam conflitantes. Isso suporta dois os mais valores diferentes de taxas de inscrição
		dependendo do período que o usuário realizou a inscrição.  </p>
		<br/>
		<ul>
			<li> <strong>Abertas:</strong> Quantidade de inscrições que foram abertas pelo coordenador.</li>
			<li> <strong>Aprovadas:</strong> Quantidade de inscrições realizadas que foram aprovadas para participação no curso ou evento. </li>
			<li> <strong>Realizadas:</strong> Quantidade de inscrições realizadas que ainda não foram aprovadas para participação no curso ou evento.  
			  <i>( Para aqueles cursos ou evento que exigem aprovação do coordenador ou pagamento de taxa de inscrição. Nos outros casos a aprovação é automática )</i> </li>
			<li> <strong>Restantes:</strong> Quantidade de vagas restantes. <i>( Quantidade Abertas - Quantidade Aprovadas - Quantidade Realizadas)</i> </li>
		</ul>
		
	</div>

	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />


	<h:form id="formListaInscricoesExtensao">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" />
			<h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preCadastrarPeriodoInscricao}" value="Abrir Período de  Inscrição" />
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Inscrição
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Suspender Inscrição
		</div>
	
	
		<table id="tabelaInscricoes"  class="listagem" style="width: 100%;">
			<caption> Períodos de Inscrições ( ${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoes} ) </caption>
		
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoes > 0}">
				<thead>
					<tr>
		 				<th rowspan="2" style="width: 25%;"> </th>
		 				<th rowspan="2"> Código </th>
						<th rowspan="2" style="text-align: right; width: 10%;"> Início do Período </th>
						<th rowspan="2" style="text-align: right; width: 10%;""> Término do Período</th>
						<th rowspan="1" colspan="4" style="text-align: center;  font-style: italic;"> Quantidade de Inscrições </th>
						<th rowspan="2" colspan="2"> </th>
					</tr>
					<tr>
						<th style="text-align: right; width: 10%;"> Abertas </th>
						<th style="text-align: right; width: 10%;"> Aprovadas </th>
						<th style="text-align: right; width: 10%;"> Realizadas </th>
						<th style="text-align: right; width: 10%;"> Restantes </th>
					</tr>
				</thead>
				
				<body>
					<c:forEach items="#{gerenciarInscricoesCursosEventosExtensaoMBean.inscricoes}" var="inscricao" varStatus="status">
			 			<tr>
			 				<td style="font-weight: bold; text-align: center;"> 
			 					${inscricao.encerrada ? ' <span style="color: red;"> ( Período de inscrição encerrado ) </span> ': ''}
			 					${inscricao.aberta ? ' <span style="color: green;"> ( Período de inscrição atual )  </span> ': ''}
			 				</td>
			 				<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: left;">  ${inscricao.codigo} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> <ufrn:format type="data" valor="${inscricao.periodoInicio}" /> </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> <ufrn:format type="data" valor="${inscricao.periodoFim}" /> </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeVagas} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeInscritosAprovados} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeInscritos}  </td>
							<td class="${inscricao.quantidadeVagasRestantes <= 0 ? 'destaquevermelho' : (inscricao.encerrada ? 'desatividada' : '') }" style="text-align: right;"> ${inscricao.quantidadeVagasRestantes} </td>
							<td style="width: 1%;"> 
								<h:commandLink id="alterarInscricao" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preAlterarPeriodoInscricao}" title="Alterar Inscrição" >
									<f:param name="idInscricaoSelecionada" value="#{inscricao.id}" />
									<h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
							</td>
							<td style="width: 1%;"> 
								<h:commandLink id="suspenderInscricao" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preSuspenderPeriodoInscricao}" title="Suspender Inscrição">
									<f:param name="idInscricaoSelecionada" value="#{inscricao.id}" />
									<h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</td>
						</tr>
			 		</c:forEach>
		 		</body>
		 		
		 	</c:if>
		 	
		 	<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoes == 0}">
		 		<tr>
					<td style="color: red; text-align: center;">Não Existem Inscrições Cadatradas </td>
				</tr>
		 	</c:if>
		 	
		 	<tfoot>
				<tr>
					<td align="center" colspan="10">
						<h:commandButton value="Cancelar" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.telaListaCursosEventosParaInscricao}" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>			
	 	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>