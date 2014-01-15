<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f"    uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>


<div class="descricaoOperacao"> 
    Esta Se��o facilita a flu�ncia das informa��es entre as fontes de informa��o e o usu�rio.<br/>
    Procura, atrav�s de servi�os fins, atender �s necessidades dos usu�rios, fornecendo-lhes a informa��o 
    desejada ou encaminhando-os para locais onde esta poder� ser encontrada.
</div>

<ul>
	
	<%-- 
	<li>@Deprecate Registrar Estat�sticas Crescimento Cole��o 
	<ul>
		<li><i>(Isso aqui vai ser registrado automaticamente ao salvar um t�tulo ou exemplar apagar as classes criadas depois)</i> 
		<h:commandLink value="Cadastrar crescimento da Cole��o" action="#{registrarCrescimentoAcervo.iniciarRegistroCrescimentoAcervo}"  onclick="setAba('informacao_referencia')"/></li>  </li>
	</ul>
	<ul>
		<li>SubItem</li>
	</ul>
	</li>
	

	<li>Solicitar Revis�o de Trabalho Acad�micos --%>
	<%-- 
	<ul>
		<li>SubItem</li>
	</ul>
	<ul>
		<li>SubItem</li>
	</ul>
	</li>
	--%>
	
	<%--
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
        			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>">
					
		<li>Levantamento Bibliogr�fico
			<ul>
				<li><h:commandLink value="Gerenciar Solicita��es de Levantamento Bibliogr�fico e Infra-estrutura" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}"  onclick="setAba('informacao_referencia')"/></li>
			</ul>
		</li>
		
	</ufrn:checkRole>
	--%>

	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
        			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">

		<li>Gerenciar Solicita��es
			<ul>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
       			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>">
					<li>
						<h:commandLink value="Agendamento de Orienta��o" action="#{solicitacaoOrientacaoMBean.verSolicitacoes}"  onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<%-- OBSERVA��O: O bibliotec�rio de cataloga��o deve acessar esse link atender as solicita��es de cataloga��o na fonte --%>
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
       			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
					<li>
						<h:commandLink value="Normaliza��o e Cataloga��o na Fonte" action="#{solicitacaoServicoDocumentoMBean.verSolicitacoes}"  onclick="setAba('informacao_referencia')"/>
					</li>
				</ufrn:checkRole>
			</ul>
		</li>

	</ufrn:checkRole>
	
	<%-- Comentado por enquanto, caso de uso foi suspenso
	<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>" >
		<li>
			<h:commandLink value="Solicita��es de Levantamento de Infra-Estrutura"
					action="#{levantamentoInfraMBean.listarParaBibliotecario}"
					 onclick="setAba('informacao_referencia')"  />
		</li>
	</ufrn:checkRole>
	--%>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		
		
		<li>Empr�stimos Institucionais
			<ul>
				<li>Bibliotecas/Unidades Internas
					<ul>
						<li>
							<h:commandLink value="Listar/Realizar Novos Empr�stimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('informacao_referencia')">
								<f:param name="emprestimoParaBibliotecaExterna" value="false" />
								<f:param name="limpaLista" value="true" />
							</h:commandLink>
						</li>
					</ul>
				</li>
				<li>Bibliotecas/Unidades Externas
					<ul>
						<li>
							<h:commandLink value="Listar/Realizar Novos Empr�stimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('informacao_referencia')">
								<f:param name="emprestimoParaBibliotecaExterna" value="true" />
								<f:param name="limpaLista" value="true" />
							</h:commandLink>
						</li>
					</ul>
				</li>
				
				
				<%-- Mesmos gerenciamento de circula��o podem ser feitos aqui para bibliotecas --%>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Gerenciar Suspens�es" action="#{suspensaoUsuarioBibliotecaMBean.iniciarGerenciaSuspensoesBibliotecas}" onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Gerenciar Multas" action="#{multasUsuarioBibliotecaMBean.iniciarGerenciarMultasBibliotecas}" onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<li>
					<h:commandLink value="Bloquear/Desbloquear Usu�rios" action="#{bloquearUsuarioBibliotecaMBean.iniciarBloqueioBibliotecas}"  onclick="setAba('informacao_referencia')" />
				</li>
				
			</ul>
				
		</li>
		
		<li>Bibliotecas/Unidades Externas
			<ul>
				<li><h:commandLink action="#{bibliotecaExternaMBean.listar}" onclick="setAba('informacao_referencia')" value="Listar / Cadastrar Nova Biblioteca ou Unidade Externa" /></li>
			</ul>
		</li>
		
	</ufrn:checkRole>
	
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li> Transfer�ncia de Materiais
			<ul>
				
				<li> <h:commandLink action="#{transfereExemplaresEntreBibliotecasMBean.iniciarTransferencia}" value="Transferir Exemplares entre Bibliotecas"
						onclick="setAba('informacao_referencia')" id="cmdTransferirExemplaresEntreBibliotecasInfRef"/> </li>
				
			</ul>	
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Materiais Perdidos
			<ul>
				<li>
					<h:commandLink value="Comunicar Material Perdido" action="#{comunicarMaterialPerdidoMBean.iniciarComunicacaoBiblioteca}" onclick="setAba('informacao_referencia')"/>
				</li>
				<li>
					<h:commandLink value="Listar Comunica��es de Materiais Perdidos" action="#{comunicarMaterialPerdidoMBean.listarBibliotecasComMateriaisPerdidos}" onclick="setAba('informacao_referencia')" />
				</li>
			</ul>
			
		</li>
	</ufrn:checkRole>
	
	<%--  <li>Registrar Estat�sticas Material
		<ul>
			<li><h:commandLink value="Cadastrar Movimenta��o de Consultas Di�rias" action="#{movimentDiarioConsultaMateriais.iniciarCadastroMovimentacaoDiaria}"  onclick="setAba('informacao_referencia')"/></li>
			<li><h:commandLink value="Cadastrar Movimenta��o de Consultas Di�rias Usando Leitor �tico" action="#{registroConsultaMaterialLeitor.preCadastrar}"  onclick="setAba('informacao_referencia')"/></li>
		</ul>
	</li>  --%>

</ul>