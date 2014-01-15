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
	<h2><ufrn:subSistema /> &gt; Lista de Inscri��es de ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividades' : 'Mini Atividade'}</h2>
	
	<div class="descricaoOperacao">
		<p>Caro (a) Usu�rio (a),</p>
		<p>Abaixo est�o listadas as inscri��es existentes para a
			
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				 Atividade: <strong> ${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.titulo} </strong> 
			</c:if>
			
			<c:if test="${! gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				 Mini Atividade: <strong> ${gerenciarInscricoesCursosEventosExtensaoMBean.subAtividadeSelecionada.titulo} </strong> 
			</c:if>
		</p>
		<br />
		<p> Por meio dessa opera��o � poss�vel criar novos per�odos de inscri��o, alterar os dados de um per�odo de inscri��o, suspender alguma inscri��o, entre outras. </p>
		<p> Os usu�rios devem ser orientados a acessarem a �rea p�blica do sistema para realizar a inscri��o. </p>
		<br/>
		<p>  <strong>IMPORTANTE:</strong> � poss�vel abrir mais de um per�odo de inscri��o, desde que os per�odos n�o sejam conflitantes. Isso suporta dois os mais valores diferentes de taxas de inscri��o
		dependendo do per�odo que o usu�rio realizou a inscri��o.  </p>
		<br/>
		<ul>
			<li> <strong>Abertas:</strong> Quantidade de inscri��es que foram abertas pelo coordenador.</li>
			<li> <strong>Aprovadas:</strong> Quantidade de inscri��es realizadas que foram aprovadas para participa��o no curso ou evento. </li>
			<li> <strong>Realizadas:</strong> Quantidade de inscri��es realizadas que ainda n�o foram aprovadas para participa��o no curso ou evento.  
			  <i>( Para aqueles cursos ou evento que exigem aprova��o do coordenador ou pagamento de taxa de inscri��o. Nos outros casos a aprova��o � autom�tica )</i> </li>
			<li> <strong>Restantes:</strong> Quantidade de vagas restantes. <i>( Quantidade Abertas - Quantidade Aprovadas - Quantidade Realizadas)</i> </li>
		</ul>
		
	</div>

	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />


	<h:form id="formListaInscricoesExtensao">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" />
			<h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preCadastrarPeriodoInscricao}" value="Abrir Per�odo de  Inscri��o" />
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Inscri��o
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Suspender Inscri��o
		</div>
	
	
		<table id="tabelaInscricoes"  class="listagem" style="width: 100%;">
			<caption> Per�odos de Inscri��es ( ${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoes} ) </caption>
		
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoes > 0}">
				<thead>
					<tr>
		 				<th rowspan="2" style="width: 25%;"> </th>
		 				<th rowspan="2"> C�digo </th>
						<th rowspan="2" style="text-align: right; width: 10%;"> In�cio do Per�odo </th>
						<th rowspan="2" style="text-align: right; width: 10%;""> T�rmino do Per�odo</th>
						<th rowspan="1" colspan="4" style="text-align: center;  font-style: italic;"> Quantidade de Inscri��es </th>
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
			 					${inscricao.encerrada ? ' <span style="color: red;"> ( Per�odo de inscri��o encerrado ) </span> ': ''}
			 					${inscricao.aberta ? ' <span style="color: green;"> ( Per�odo de inscri��o atual )  </span> ': ''}
			 				</td>
			 				<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: left;">  ${inscricao.codigo} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> <ufrn:format type="data" valor="${inscricao.periodoInicio}" /> </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> <ufrn:format type="data" valor="${inscricao.periodoFim}" /> </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeVagas} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeInscritosAprovados} </td>
							<td class="${inscricao.encerrada || inscricao.quantidadeVagasRestantes <= 0 ? 'desatividada' : '' }" style="text-align: right;"> ${inscricao.quantidadeInscritos}  </td>
							<td class="${inscricao.quantidadeVagasRestantes <= 0 ? 'destaquevermelho' : (inscricao.encerrada ? 'desatividada' : '') }" style="text-align: right;"> ${inscricao.quantidadeVagasRestantes} </td>
							<td style="width: 1%;"> 
								<h:commandLink id="alterarInscricao" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preAlterarPeriodoInscricao}" title="Alterar Inscri��o" >
									<f:param name="idInscricaoSelecionada" value="#{inscricao.id}" />
									<h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
							</td>
							<td style="width: 1%;"> 
								<h:commandLink id="suspenderInscricao" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.preSuspenderPeriodoInscricao}" title="Suspender Inscri��o">
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
					<td style="color: red; text-align: center;">N�o Existem Inscri��es Cadatradas </td>
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