<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<style>
	#abas-relatorio div.yui-ext-tabitembody{
		background: #EAF3FD;
		padding: 5px 15px;
	}

	#abas-relatorio textarea {
		width: 98%;
		margin: 0 auto;
	}

	p.descricao {
		padding: 5px 100px 10px;
		font-style: italic;
		text-align: center;
	}
	table.subFormulario tr.titulo {
		border: 1px solid #CCC;
		border-width: 1px 0;
	}
</style>

<h2>
	<ufrn:subSistema /> &gt; Atendimento ao Aluno
</h2>
<div id="operacaoAjuda" class="descricaoOperacao" style="display:none"><a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('operacaoAjuda').hide();$('ajuda').show();" href="javascript://nop/">  (^) mostrar ajuda </a></div>
<div id="ajuda" class="descricaoOperacao">
	<p>
	<strong>Atendimento ao Aluno</strong> é um canal de comunicação entre o estudante e a coordenação.
	</p>
	<p>A mensagem de resposta será enviada para o email do aluno cadastrado no SIGAA.</p>
</div>


		<h:form id="novaPergunta">
			<br/>
			
			<div class="infoAltRem" style="font-variant: small-caps;">
				Resposta será enviada para <br/>${atendimentoAluno.obj.discente.nome} 
				(<h:commandLink action="#{ buscaAvancadaDiscenteMBean.detalhesDiscente }" id="historico" value="Ver Histórico">
					<f:param name="id" value="#{ atendimentoAluno.obj.discente.id }"/>
				</h:commandLink>)
			</div>
			
	    	<table class="subFormulario" width="100%">
		   	    <tbody>
	    			<tr>
						<td class="subFormulario">
							Título da Pergunta
						</td>
		   	    	</tr>   	
		   	    	<tr>
						<td>
		   	    			<p style="margin-left: 10px;padding-left: 2px">${atendimentoAluno.obj.titulo}</p>
		   	    		</td>			   	    	
		   	    	</tr>
		   	    	<tr>		
		   	    		<td class="subFormulario">
		   	    			Pergunta
		   	    		</td>
					</tr>
					<tr>
						<td>
		   	    			<p style="margin-left: 10px;padding-left: 2px">${atendimentoAluno.obj.pergunta}</p>
		   	    		</td>
	    			</tr>		   	    
					<tr class="titulo">
						<td colspan="2">
							<p class="descricao">
								Resposta ao aluno <span class="required"> </span>
							</p>
							<p>
								<center><h:inputTextarea id="resposta" value="#{atendimentoAluno.obj.resposta}" rows="6" style="width: 90%" /></center>
							</p>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="gravar" value="Enviar" action="#{atendimentoAluno.responder}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{atendimentoAluno.cancelar}" />
			    		</td>
			    	</tr>
				</tfoot>			
			</table>
		</h:form>
				<br/>
		
		<center>
			<img style="vertical-align: top;" src="/sigaa/img/required.gif"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
		</center>
				
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>